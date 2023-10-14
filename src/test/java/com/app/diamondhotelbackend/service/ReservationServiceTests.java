package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.reservation.request.ReservationCreateRequestDto;
import com.app.diamondhotelbackend.dto.room.model.RoomSelected;
import com.app.diamondhotelbackend.entity.*;
import com.app.diamondhotelbackend.repository.ReservationRepository;
import com.app.diamondhotelbackend.service.email.EmailServiceImpl;
import com.app.diamondhotelbackend.service.flight.FlightServiceImpl;
import com.app.diamondhotelbackend.service.payment.PaymentServiceImpl;
import com.app.diamondhotelbackend.service.reservation.ReservationServiceImpl;
import com.app.diamondhotelbackend.service.reservedroom.ReservedRoomServiceImpl;
import com.app.diamondhotelbackend.service.room.RoomServiceImpl;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.util.PdfUtil;
import com.app.diamondhotelbackend.util.QrCodeUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTests {

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserProfileServiceImpl userProfileService;

    @Mock
    private FlightServiceImpl flightService;

    @Mock
    private PaymentServiceImpl paymentService;

    @Mock
    private RoomServiceImpl roomService;

    @Mock
    private ReservedRoomServiceImpl reservedRoomService;

    @Mock
    private EmailServiceImpl emailService;

    @Mock
    private QrCodeUtil qrCodeUtil;

    @Mock
    private PdfUtil pdfUtil;

    private UserProfile userProfile;

    private Flight flight;

    private Payment payment;

    private ReservationCreateRequestDto reservationCreateRequestDto;

    private byte[] qrCode;

    private InputStreamResource inputStreamResource;

    private Reservation reservation;

    private List<Reservation> reservationList;

    private Page<Reservation> reservationPage;

    private List<Room> roomList;

    private ReservedRoom reservedRoom;

    @BeforeEach
    public void init() {
        RoomType roomType = RoomType.builder()
                .id(2)
                .pricePerHotelNight(BigDecimal.valueOf(200))
                .build();

        List<RoomSelected> roomSelectedList = List.of(
                RoomSelected.builder()
                        .roomTypeId(2)
                        .rooms(1)
                        .build()
        );

        userProfile = UserProfile.builder()
                .id(1)
                .email("email1")
                .passportNumber("passportNumber1")
                .build();

        flight = Flight.builder()
                .id(1)
                .flightNumber("flightNumber1")
                .build();

        qrCode = new byte[0];

        inputStreamResource = new InputStreamResource(new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        });

        payment = Payment.builder()
                .id(1)
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .cost(BigDecimal.valueOf(600))
                .build();

        reservationCreateRequestDto = ReservationCreateRequestDto.builder()
                .userProfileId(1)
                .checkIn("2023-12-24")
                .checkOut("2023-12-27")
                .adults(2)
                .children(2)
                .flightNumber("flightNumber1")
                .roomSelectedList(roomSelectedList)
                .build();

        reservation = Reservation.builder()
                .id(1)
                .checkIn(Date.valueOf("2023-12-24"))
                .checkOut(Date.valueOf("2023-12-27"))
                .userProfile(userProfile)
                .flight(flight)
                .payment(payment)
                .build();

        reservationList = List.of(
                Reservation.builder()
                        .id(1)
                        .checkIn(Date.valueOf("2023-10-24"))
                        .checkOut(Date.valueOf("2023-10-27"))
                        .userProfile(userProfile)
                        .flight(flight)
                        .payment(payment)
                        .build(),
                Reservation.builder()
                        .id(2)
                        .checkIn(Date.valueOf("2023-11-24"))
                        .checkOut(Date.valueOf("2023-11-27"))
                        .userProfile(userProfile)
                        .flight(flight)
                        .payment(payment)
                        .build(),
                Reservation.builder().id(3)
                        .checkIn(Date.valueOf("2023-12-24"))
                        .checkOut(Date.valueOf("2023-12-27"))
                        .userProfile(userProfile)
                        .flight(flight)
                        .payment(payment)
                        .build()
        );

        reservationPage = new PageImpl<>(reservationList);

        roomList = List.of(
                Room.builder()
                        .id(1)
                        .roomType(roomType)
                        .build(),
                Room.builder()
                        .id(2)
                        .roomType(roomType)
                        .build()
        );

        reservedRoom = ReservedRoom.builder()
                .id(1)
                .room(roomList.get(0))
                .reservation(reservation)
                .cost(BigDecimal.valueOf(600))
                .build();
    }

    @Test
    public void ReservationService_CreateReservation_ReturnsReservation() throws IOException {
        when(reservationRepository.save(Mockito.any(Reservation.class))).thenReturn(reservation);
        when(userProfileService.getUserProfileById(Mockito.any(long.class))).thenReturn(userProfile);
        when(paymentService.createPayment(Mockito.any(Payment.class))).thenReturn(payment);
        when(roomService.getRoomAvailableList(Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(RoomSelected.class))).thenReturn(roomList);
        when(reservedRoomService.createReservedRoom(Mockito.any(Reservation.class), Mockito.any(Room.class))).thenReturn(reservedRoom);
        when(paymentService.updatePaymentCost(Mockito.any(long.class), Mockito.any(BigDecimal.class))).thenReturn(payment);
        when(qrCodeUtil.getQRCode(Mockito.any(String.class), Mockito.any(int.class), Mockito.any(int.class))).thenReturn(qrCode);
        when(pdfUtil.getReservationPdf(Mockito.any(Reservation.class), Mockito.anyList(), Mockito.any(byte[].class))).thenReturn(inputStreamResource);

        Reservation savedReservation = reservationService.createReservation(reservationCreateRequestDto);

        verify(emailService).sendReservationConfirmedEmail(Mockito.any(Reservation.class), Mockito.any(InputStreamResource.class));

        Assertions.assertThat(savedReservation).isNotNull();
        Assertions.assertThat(savedReservation.getId()).isEqualTo(reservation.getId());
    }

    @Test
    public void ReservationService_GetReservationList_ReturnsReservationList() {
        when(reservationRepository.findAll(Mockito.any(PageRequest.class))).thenReturn(reservationPage);

        List<Reservation> foundReservationList = reservationService.getReservationList(1, 3);

        Assertions.assertThat(foundReservationList).isNotNull();
        Assertions.assertThat(reservationList.size()).isEqualTo(3);
    }

    @Test
    public void ReservationService_GetReservationListByUserProfileId_ReturnsReservationList() {
        when(reservationRepository.findAllByUserProfileId(Mockito.any(long.class), Mockito.any(PageRequest.class))).thenReturn(reservationPage);

        List<Reservation> foundReservationList = reservationService.getReservationListByUserProfileId(1, 1, 3,  "");

        Assertions.assertThat(foundReservationList).isNotNull();
        Assertions.assertThat(reservationList.size()).isEqualTo(3);
    }

    @Test
    public void ReservationService_CountReservationListByUserProfileId_ReturnsLong() {
        when(userProfileService.getUserProfileById(Mockito.any(long.class))).thenReturn(reservation.getUserProfile());
        when(reservationRepository.countAllByUserProfile(Mockito.any(UserProfile.class))).thenReturn(3L);

        Long countReservationList = reservationService.countReservationListByUserProfileId(1L);

        Assertions.assertThat(countReservationList).isNotNull();
        Assertions.assertThat(countReservationList).isEqualTo(3L);
    }

    @Test
    public void ReservationService_GetReservationById_ReturnsReservation() {
        when(reservationRepository.findById(Mockito.any(long.class))).thenReturn(Optional.of(reservation));

        Reservation foundReservation = reservationService.getReservationById(reservation.getId());

        Assertions.assertThat(foundReservation).isNotNull();
        Assertions.assertThat(foundReservation.getId()).isEqualTo(reservation.getId());
    }
}
