package com.app.diamondhotelbackend.service.reservation;

import com.app.diamondhotelbackend.dto.reservation.request.ReservationCreateRequestDto;
import com.app.diamondhotelbackend.dto.room.model.RoomSelected;
import com.app.diamondhotelbackend.entity.*;
import com.app.diamondhotelbackend.exception.*;
import com.app.diamondhotelbackend.repository.ReservationRepository;
import com.app.diamondhotelbackend.service.email.EmailServiceImpl;
import com.app.diamondhotelbackend.service.flight.FlightServiceImpl;
import com.app.diamondhotelbackend.service.payment.PaymentServiceImpl;
import com.app.diamondhotelbackend.service.reservedroom.ReservedRoomServiceImpl;
import com.app.diamondhotelbackend.service.room.RoomServiceImpl;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.DateUtil;
import com.app.diamondhotelbackend.util.PdfUtil;
import com.app.diamondhotelbackend.util.QrCodeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    private final UserProfileServiceImpl userProfileService;

    private final FlightServiceImpl flightService;

    private final PaymentServiceImpl paymentService;

    private final RoomServiceImpl roomService;

    private final ReservedRoomServiceImpl reservedRoomService;

    private final EmailServiceImpl emailService;

    private final QrCodeUtil qrCodeUtil;

    private final PdfUtil pdfUtil;

    @Override
    public Reservation createReservation(ReservationCreateRequestDto reservationCreateRequestDto) throws ReservationProcessingException, UserProfileProcessingException, IOException {
        Optional<Date> checkInAsDate = DateUtil.parseDate(reservationCreateRequestDto.getCheckIn());
        Optional<Date> checkOutAsDate = DateUtil.parseDate(reservationCreateRequestDto.getCheckOut());

        if (checkInAsDate.isEmpty() || checkOutAsDate.isEmpty() || reservationCreateRequestDto.getRoomSelectedList().isEmpty()) {
            throw new ReservationProcessingException(ConstantUtil.INVALID_PARAMETERS_EXCEPTION);
        }

        UserProfile userProfile = prepareUserProfile(reservationCreateRequestDto.getUserProfileId());
        Flight flight = prepareFlight(reservationCreateRequestDto.getFlightNumber());
        Payment payment = preparePayment();
        Reservation reservation = prepareReservation(checkInAsDate.get(), checkOutAsDate.get(), reservationCreateRequestDto.getAdults(), reservationCreateRequestDto.getChildren(), userProfile, flight, payment);
        List<ReservedRoom> reservedRoomList = prepareReservedRoomList(checkInAsDate.get(), checkOutAsDate.get(), reservation, payment, reservationCreateRequestDto.getRoomSelectedList());

        if (reservedRoomList.isEmpty()) {
            throw new ReservationProcessingException(ConstantUtil.NOT_ENOUGH_AVAILABLE_ROOMS);
        }

        BigDecimal cost = BigDecimal.valueOf(reservedRoomList.stream()
                .map(reservedRoom -> reservedRoom.getCost().longValue())
                .reduce(0L, Long::sum));
        payment = paymentService.updatePaymentCost(payment.getId(), cost);
        reservation.setPayment(payment);

        String text = "Reservation id: " + reservation.getId() + ", Payment: " + reservation.getPayment().getToken();
        byte[] qrCode = qrCodeUtil.getQRCode(text, 300, 300);
        InputStreamResource inputStreamResource = pdfUtil.getReservationPdf(reservation, reservedRoomList, qrCode);
        emailService.sendReservationConfirmedEmail(reservation, inputStreamResource);

        return reservation;
    }

    @Override
    public List<Reservation> getReservationList(int page, int size) {
        if (page < 0 || size < 1) {
            return Collections.emptyList();
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Reservation> reservationPage = reservationRepository.findAll(pageable);

        return reservationPage.getContent();
    }

    @Override
    public List<Reservation> getReservationListByUserProfileId(long userProfileId, int page, int size, String paymentStatus) {
        try {
            if (userProfileId < 1 || page < 0 || size < 1) {
                return Collections.emptyList();
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<Reservation> reservationPage;
            if (paymentStatus.isEmpty()) {
                reservationPage = reservationRepository.findAllByUserProfileId(userProfileId, pageable);
            } else {
                reservationPage = reservationRepository.findAllByUserProfileIdAndPaymentStatus(userProfileId, paymentStatus, pageable);
            }

            return reservationPage.getContent();

        } catch (AuthProcessingException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Long countReservationListByUserProfileId(long userProfileId) throws UserProfileProcessingException {
        UserProfile userProfile = userProfileService.getUserProfileById(userProfileId);

        return reservationRepository.countAllByUserProfile(userProfile);
    }

    @Override
    public Reservation getReservationById(long id) throws ReservationProcessingException {
        return reservationRepository.findById(id).orElseThrow(() -> new ReservationProcessingException(ConstantUtil.RESERVATION_NOT_FOUND_EXCEPTION));
    }

    @Override
    public InputStreamResource getReservationPdfDocument(long id) throws ReservationProcessingException {
        Reservation reservation = getReservationById(id);
        List<ReservedRoom> reservedRoomList = reservedRoomService.getReservedRoomListByReservationId(id);
        String text = "Reservation id: " + reservation.getId() + ", Payment: " + reservation.getPayment().getToken();
        byte[] qrCode = qrCodeUtil.getQRCode(text, 250, 250);

        return pdfUtil.getReservationPdf(reservation, reservedRoomList, qrCode);
    }

    private UserProfile prepareUserProfile(long userProfileId) throws UserProfileProcessingException {
        return userProfileService.getUserProfileById(userProfileId);
    }

    private Flight prepareFlight(String flightNumber) {
        if (!flightService.isValidFlightNumber(flightNumber)) {
            return null;
        }

        try {
            return flightService.getFlightByFlightNumber(flightNumber);
        } catch (FlightProcessingException e) {
            return flightService.createFlight(Flight.builder().flightNumber(flightNumber).build());
        }
    }

    private Payment preparePayment() {
        Payment payment = Payment.builder()
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .status(ConstantUtil.WAITING_FOR_PAYMENT)
                .build();

        return paymentService.createPayment(payment);
    }

    private Reservation prepareReservation(Date checkIn, Date checkOut, int adults, int children, UserProfile userProfile, Flight flight, Payment payment) {
        Reservation reservation = Reservation.builder()
                .checkIn(checkIn)
                .checkOut(checkOut)
                .adults(adults)
                .children(children)
                .userProfile(userProfile)
                .flight(flight)
                .payment(payment)
                .build();

        return reservationRepository.save(reservation);
    }

    private List<ReservedRoom> prepareReservedRoomList(Date checkIn, Date checkOut, Reservation reservation, Payment payment, List<RoomSelected> roomSelectedList) {
        try {
            List<ReservedRoom> reservedRoomList = new ArrayList<>();
            for (RoomSelected roomSelected : roomSelectedList) {
                reservedRoomList.addAll(
                        roomService.getRoomAvailableList(checkIn, checkOut, roomSelected).stream()
                                .map(room -> reservedRoomService.createReservedRoom(reservation, room))
                                .toList()
                );
            }

            return reservedRoomList;

        } catch (RoomProcessingException e) {
            paymentService.updatePaymentStatus(payment.getId(), ConstantUtil.CANCELLED);

            return Collections.emptyList();
        }
    }
}
