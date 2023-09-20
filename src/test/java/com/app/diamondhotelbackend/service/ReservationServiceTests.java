package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.reservation.request.ReservationCreateRequestDto;
import com.app.diamondhotelbackend.dto.room.model.RoomSelected;
import com.app.diamondhotelbackend.entity.*;
import com.app.diamondhotelbackend.repository.ReservationRepository;
import com.app.diamondhotelbackend.service.flight.FlightServiceImpl;
import com.app.diamondhotelbackend.service.payment.PaymentServiceImpl;
import com.app.diamondhotelbackend.service.reservation.ReservationServiceImpl;
import com.app.diamondhotelbackend.service.reservedroom.ReservedRoomServiceImpl;
import com.app.diamondhotelbackend.service.room.RoomServiceImpl;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

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

    private UserProfile userProfile;

    private Flight flight;

    private Payment payment;

    private ReservationCreateRequestDto reservationCreateRequestDto;

    private Reservation reservation;

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
    public void ReservationService_createReservation_ReturnsReservation_Case1() {
        when(reservationRepository.save(Mockito.any(Reservation.class))).thenReturn(reservation);
        when(userProfileService.getUserProfileById(Mockito.any(long.class))).thenReturn(userProfile);
        when(flightService.getFlightByFlightNumber(Mockito.any(String.class))).thenReturn(flight);
        when(paymentService.createPayment(Mockito.any(Payment.class))).thenReturn(payment);
        when(roomService.getRoomAvailableList(Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(RoomSelected.class))).thenReturn(roomList);
        when(reservedRoomService.createReservedRoom(Mockito.any(Reservation.class), Mockito.any(Room.class))).thenReturn(reservedRoom);
        when(paymentService.updatePaymentCost(Mockito.any(long.class), Mockito.any(BigDecimal.class))).thenReturn(payment);

        Reservation savedReservation = reservationService.createReservation(reservationCreateRequestDto);

        Assertions.assertThat(savedReservation).isNotNull();
        Assertions.assertThat(savedReservation.getId()).isEqualTo(reservation.getId());
    }

    @Test
    public void ReservationService_GetReservationById_ReturnsReservation() {
        when(reservationRepository.findById(Mockito.any(long.class))).thenReturn(Optional.of(reservation));

        Reservation foundReservation = reservationService.getReservationById(reservation.getId());

        Assertions.assertThat(foundReservation).isNotNull();
        Assertions.assertThat(foundReservation.getId()).isEqualTo(reservation.getId());
    }
}
