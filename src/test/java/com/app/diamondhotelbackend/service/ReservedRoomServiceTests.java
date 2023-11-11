package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.entity.*;
import com.app.diamondhotelbackend.repository.ReservedRoomRepository;
import com.app.diamondhotelbackend.service.reservedroom.ReservedRoomServiceImpl;
import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservedRoomServiceTests {

    @InjectMocks
    private ReservedRoomServiceImpl reservedRoomService;

    @Mock
    private ReservedRoomRepository reservedRoomRepository;

    private Reservation reservation;

    private Room room;

    private ReservedRoom reservedRoom;

    private List<ReservedRoom> reservedRoomList;

    private Page<ReservedRoom> reservedRoomPage;


    @BeforeEach
    public void init() {
        RoomType roomType = RoomType.builder()
                .id(1)
                .name("Deluxe suite")
                .pricePerHotelNight(BigDecimal.valueOf(350))
                .build();

        UserProfile userProfile = UserProfile.builder()
                .id(1)
                .email("test.email@gmail.com")
                .passportNumber("AD1234")
                .build();

        reservation = Reservation.builder()
                .id(1)
                .userProfile(userProfile)
                .checkIn(Date.valueOf("2023-12-24"))
                .checkOut(Date.valueOf("2023-12-27"))
                .build();

        room = Room.builder()
                .id(1)
                .roomType(roomType)
                .build();

        reservedRoom = ReservedRoom.builder()
                .id(1)
                .room(room)
                .reservation(reservation)
                .build();

        reservedRoomList = List.of(
                ReservedRoom.builder()
                        .id(1)
                        .build(),
                ReservedRoom.builder()
                        .id(2)
                        .build()
        );

        reservedRoomPage = new PageImpl<>(reservedRoomList);
    }

    @Test
    public void ReservedRoomService_CreateReservedRoom_ReturnsReservedRoom() {
        when(reservedRoomRepository.save(Mockito.any(ReservedRoom.class))).thenReturn(reservedRoom);

        ReservedRoom savedReservedRoom = reservedRoomService.createReservedRoom(reservation, room);

        Assertions.assertThat(savedReservedRoom).isNotNull();
        Assertions.assertThat(savedReservedRoom.getId()).isEqualTo(reservedRoom.getId());
    }

    @Test
    public void ReservationService_GetReservedRoomList_ReturnsReservationList() {
        when(reservedRoomRepository.findAll(Mockito.any(Specification.class), Mockito.any(PageRequest.class))).thenReturn(reservedRoomPage);

        List<ReservedRoom> foundReservedRoomList = reservedRoomService.getReservedRoomList(1, 3, new JSONObject(), new JSONArray());

        Assertions.assertThat(foundReservedRoomList).isNotNull();
        Assertions.assertThat(foundReservedRoomList.size()).isEqualTo(2);
    }

    @Test
    public void ReservedRoomService_GetReservedRoomListByReservationCheckInAndReservationCheckOut_ReturnsReservedRoomList() {
        when(reservedRoomRepository.findAllByReservationCheckInAndReservationCheckOut(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(reservedRoomList);

        List<ReservedRoom> foundReservedRoomList = reservedRoomService.getReservedRoomListByReservationCheckInAndReservationCheckOut(Date.valueOf("2023-09-20"), Date.valueOf("2023-09-25"));

        Assertions.assertThat(foundReservedRoomList).isNotNull();
        Assertions.assertThat(foundReservedRoomList.size()).isEqualTo(2);
    }

    @Test
    public void ReservedRoomService_GetReservedRoomListByReservationId_ReturnsReservedRoomList() {
        when(reservedRoomRepository.findAll(Mockito.any(Specification.class))).thenReturn(reservedRoomList);

        List<ReservedRoom> foundReservedRoomList = reservedRoomService.getReservedRoomListByReservationId(1);

        Assertions.assertThat(foundReservedRoomList).isNotNull();
        Assertions.assertThat(foundReservedRoomList.size()).isEqualTo(2);
    }

    @Test
    public void ReservedRoomService_GetReservedRoomListByUserProfileId_ReturnsReservedRoomList() {
        when(reservedRoomRepository.findAll(Mockito.any(Specification.class), Mockito.any(PageRequest.class))).thenReturn(reservedRoomPage);

        List<ReservedRoom> foundReservedRoomList = reservedRoomService.getReservedRoomListByUserProfileId(1L, 0, 3, new JSONObject(), new JSONArray());

        Assertions.assertThat(foundReservedRoomList).isNotNull();
        Assertions.assertThat(foundReservedRoomList.size()).isEqualTo(2);
    }

    @Test
    public void ReservedRoomService_Count_Long() {
        when(reservedRoomRepository.count()).thenReturn(2L);

        Long countReservationList = reservedRoomService.countReservedRoomList();

        Assertions.assertThat(countReservationList).isNotNull();
        Assertions.assertThat(countReservationList).isEqualTo(2L);
    }

    @Test
    public void ReservedRoomService_CountReservedRoomListByUserProfileId_Long() {
        when(reservedRoomRepository.count(Mockito.any(Specification.class))).thenReturn(2L);

        Long countReservationList = reservedRoomService.countReservedRoomListByUserProfileId(1L);

        Assertions.assertThat(countReservationList).isNotNull();
        Assertions.assertThat(countReservationList).isEqualTo(2L);
    }
}
