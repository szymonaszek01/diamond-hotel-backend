package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.ReservedRoom;
import com.app.diamondhotelbackend.entity.Room;
import com.app.diamondhotelbackend.entity.RoomType;
import com.app.diamondhotelbackend.repository.ReservedRoomRepository;
import com.app.diamondhotelbackend.service.reservedroom.ReservedRoomServiceImpl;
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

    @BeforeEach
    public void init() {
        RoomType roomType = RoomType.builder()
                .id(1)
                .name("Deluxe suite")
                .pricePerHotelNight(BigDecimal.valueOf(350))
                .build();

        reservation = Reservation.builder()
                .id(1)
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
    }

    @Test
    public void ReservedRoomService_CreateReservedRoom_ReturnsReservedRoom() {
        when(reservedRoomRepository.save(Mockito.any(ReservedRoom.class))).thenReturn(reservedRoom);

        ReservedRoom savedReservedRoom = reservedRoomService.createReservedRoom(reservation, room);

        Assertions.assertThat(savedReservedRoom).isNotNull();
        Assertions.assertThat(savedReservedRoom.getId()).isEqualTo(reservedRoom.getId());
    }

    @Test
    public void ReservedRoomService_GetAllByReservationId_ReturnsReservedRoomList() {
        when(reservedRoomRepository.findAllByReservationId(Mockito.any(long.class))).thenReturn(reservedRoomList);

        List<ReservedRoom> foundReservedRoomList = reservedRoomService.getReservedRoomListByReservationId(1);

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
}
