package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.room.response.RoomAvailableResponseDto;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.ReservedRoom;
import com.app.diamondhotelbackend.entity.Room;
import com.app.diamondhotelbackend.entity.RoomType;
import com.app.diamondhotelbackend.repository.RoomRepository;
import com.app.diamondhotelbackend.service.reservedroom.ReservedRoomServiceImpl;
import com.app.diamondhotelbackend.service.room.RoomServiceImpl;
import com.app.diamondhotelbackend.service.roomtype.RoomTypeServiceImpl;
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
public class RoomServiceTests {

    @InjectMocks
    private RoomServiceImpl roomService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ReservedRoomServiceImpl reservedRoomService;

    @Mock
    private RoomTypeServiceImpl roomTypeService;

    private List<Room> roomList;

    private List<ReservedRoom> reservedRoomList;

    private RoomType roomType;

    private String checkIn;

    private String checkOut;

    private int rooms;

    private List<Long> roomTypeIdList;

    private double pricePerHotelNight;

    @BeforeEach
    public void init() {
        roomType = RoomType.builder()
                .id(1)
                .name("Family room")
                .adults(2)
                .children(2)
                .pricePerHotelNight(BigDecimal.valueOf(100))
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

        List<Reservation> reservationList = List.of(
                Reservation.builder()
                        .checkIn(Date.valueOf("2023-09-20"))
                        .checkOut(Date.valueOf("2023-09-25"))
                        .build(),
                Reservation.builder()
                        .checkIn(Date.valueOf("2023-12-20"))
                        .checkOut(Date.valueOf("2023-12-25"))
                        .build()
        );

        reservedRoomList = List.of(
                ReservedRoom.builder()
                        .id(1)
                        .reservation(reservationList.get(0))
                        .room(roomList.get(0))
                        .build()
        );

        checkIn = "2023-09-20";

        checkOut = "2023-09-25";

        rooms = 1;

        roomTypeIdList = List.of(1L);

        pricePerHotelNight = 200;
    }

    @Test
    public void RoomService_GetRoomAvailableList_ReturnsRoomList_Case1() {
        when(roomRepository.findAllByRoomTypeIdInAndRoomTypePricePerHotelNightLessThanEqual(Mockito.anyList(), Mockito.any(BigDecimal.class))).thenReturn(roomList);
        when(reservedRoomService.getReservedRoomListByReservationCheckInAndReservationCheckOut(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(reservedRoomList);
        when(roomTypeService.getRoomTypeIdList()).thenReturn(roomTypeIdList);
        when(roomTypeService.getRoomTypeById(Mockito.any(long.class))).thenReturn(roomType);

        RoomAvailableResponseDto foundRoomAvailableResponseDto = roomService.getRoomAvailableList(checkIn, checkOut, rooms, 2, 2, roomTypeIdList, pricePerHotelNight);

        Assertions.assertThat(foundRoomAvailableResponseDto).isNotNull();
        Assertions.assertThat(foundRoomAvailableResponseDto.getRoomAvailabilityList().size()).isEqualTo(1);
    }

    @Test
    public void RoomService_GetRoomAvailableList_ReturnsRoomList_Case2() {
        when(roomRepository.findAllByRoomTypeIdIn(Mockito.anyList())).thenReturn(roomList);
        when(reservedRoomService.getReservedRoomListByReservationCheckInAndReservationCheckOut(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(reservedRoomList);
        when(roomTypeService.getRoomTypeIdList()).thenReturn(roomTypeIdList);
        when(roomTypeService.getRoomTypeById(Mockito.any(long.class))).thenReturn(roomType);

        RoomAvailableResponseDto foundRoomAvailableResponseDto = roomService.getRoomAvailableList(checkIn, checkOut, rooms, 2, 2, roomTypeIdList, 0);

        Assertions.assertThat(foundRoomAvailableResponseDto).isNotNull();
        Assertions.assertThat(foundRoomAvailableResponseDto.getRoomAvailabilityList().size()).isEqualTo(1);
    }

    @Test
    public void RoomService_GetRoomAvailableList_ReturnsRoomList_Case3() {
        when(roomRepository.findAllByRoomTypePricePerHotelNightLessThanEqual(Mockito.any(BigDecimal.class))).thenReturn(roomList);
        when(reservedRoomService.getReservedRoomListByReservationCheckInAndReservationCheckOut(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(reservedRoomList);
        when(roomTypeService.getRoomTypeIdList()).thenReturn(roomTypeIdList);
        when(roomTypeService.getRoomTypeById(Mockito.any(long.class))).thenReturn(roomType);

        RoomAvailableResponseDto foundRoomAvailableResponseDto = roomService.getRoomAvailableList(checkIn, checkOut, rooms, 2, 2, null, pricePerHotelNight);

        Assertions.assertThat(foundRoomAvailableResponseDto).isNotNull();
        Assertions.assertThat(foundRoomAvailableResponseDto.getRoomAvailabilityList().size()).isEqualTo(1);
    }

    @Test
    public void RoomService_GetRoomAvailableList_ReturnsRoomList_Case4() {
        when(roomRepository.findAll()).thenReturn(roomList);
        when(reservedRoomService.getReservedRoomListByReservationCheckInAndReservationCheckOut(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(reservedRoomList);
        when(roomTypeService.getRoomTypeIdList()).thenReturn(roomTypeIdList);
        when(roomTypeService.getRoomTypeById(Mockito.any(long.class))).thenReturn(roomType);

        RoomAvailableResponseDto foundRoomAvailableResponseDto = roomService.getRoomAvailableList(checkIn, checkOut, rooms, 2, 2, null, 0);

        Assertions.assertThat(foundRoomAvailableResponseDto).isNotNull();
        Assertions.assertThat(foundRoomAvailableResponseDto.getRoomAvailabilityList().size()).isEqualTo(1);
    }
}
