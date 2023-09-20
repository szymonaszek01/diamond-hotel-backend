package com.app.diamondhotelbackend.service.reservation;

import com.app.diamondhotelbackend.dto.reservation.request.ReservationCreateRequestDto;
import com.app.diamondhotelbackend.dto.room.model.RoomSelected;
import com.app.diamondhotelbackend.entity.*;
import com.app.diamondhotelbackend.exception.FlightProcessingException;
import com.app.diamondhotelbackend.exception.ReservationProcessingException;
import com.app.diamondhotelbackend.exception.RoomProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.repository.ReservationRepository;
import com.app.diamondhotelbackend.service.flight.FlightServiceImpl;
import com.app.diamondhotelbackend.service.reservedroom.ReservedRoomServiceImpl;
import com.app.diamondhotelbackend.service.room.RoomServiceImpl;
import com.app.diamondhotelbackend.service.payment.PaymentServiceImpl;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @Override
    public Reservation createReservation(ReservationCreateRequestDto reservationCreateRequestDto) throws ReservationProcessingException, UserProfileProcessingException {
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

        return reservation;
    }

    @Override
    public Reservation getReservationById(long id) throws ReservationProcessingException {
        return reservationRepository.findById(id).orElseThrow(() -> new ReservationProcessingException(ConstantUtil.RESERVATION_NOT_FOUND_EXCEPTION));
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
