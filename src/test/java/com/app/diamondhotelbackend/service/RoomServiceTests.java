package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.room.model.RoomSelected;
import com.app.diamondhotelbackend.dto.room.request.AddRoomRequestDto;
import com.app.diamondhotelbackend.dto.room.response.RoomAvailableResponseDto;
import com.app.diamondhotelbackend.dto.room.response.RoomDetailsDto;
import com.app.diamondhotelbackend.dto.room.response.RoomSelectedCostResponseDto;
import com.app.diamondhotelbackend.entity.*;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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

    private RoomSelected roomSelected;

    private AddRoomRequestDto addRoomRequestDto;

    private Room room;

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
                        .checkIn(Date.valueOf("2024-09-20"))
                        .checkOut(Date.valueOf("2024-09-25"))
                        .userProfile(UserProfile.builder().id(1).email("szymon-jakubaszek@wp.pl").build())
                        .build(),
                Reservation.builder()
                        .checkIn(Date.valueOf("2024-12-20"))
                        .checkOut(Date.valueOf("2024-12-25"))
                        .userProfile(UserProfile.builder().id(1).email("szymon-jakubaszek@wp.pl").build())
                        .build()
        );

        reservedRoomList = List.of(
                ReservedRoom.builder()
                        .id(1)
                        .reservation(reservationList.get(0))
                        .room(roomList.get(0))
                        .build()
        );

        checkIn = "2024-09-20";

        checkOut = "2024-09-25";

        rooms = 1;

        roomTypeIdList = List.of(1L);

        pricePerHotelNight = 200;

        roomSelected = RoomSelected.builder()
                .roomTypeId(1)
                .rooms(1)
                .build();

        addRoomRequestDto = AddRoomRequestDto.builder()
                .number(123)
                .floor(2)
                .roomTypeId(3)
                .build();

        room = Room.builder()
                .number(123)
                .floor(2)
                .roomType(RoomType.builder().name("Deluxe Suite").build())
                .build();
    }

    @Test
    public void RoomService_CreateRoom_ReturnsRoom() {
        when(roomRepository.save((Mockito.any(Room.class)))).thenReturn(room);

        Room savedRoom = roomService.createRoom(addRoomRequestDto);

        Assertions.assertThat(savedRoom).isNotNull();
        Assertions.assertThat(savedRoom.getNumber()).isEqualTo(123);
    }

    @Test
    public void RoomService_GetRoomAvailableList_ReturnsRoomAvailableResponseDto_Case1() {
        when(roomRepository.findAllByRoomTypeIdInAndRoomTypePricePerHotelNightLessThanEqual(Mockito.anyList(), Mockito.any(BigDecimal.class))).thenReturn(roomList);
        when(reservedRoomService.getReservedRoomListByReservationCheckInAndReservationCheckOut(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(reservedRoomList);
        when(roomTypeService.getRoomTypeIdList()).thenReturn(roomTypeIdList);
        when(roomTypeService.getRoomTypeById(Mockito.any(long.class))).thenReturn(roomType);

        RoomAvailableResponseDto foundRoomAvailableResponseDto = roomService.getRoomAvailableList(checkIn, checkOut, rooms, 2, 2, roomTypeIdList, pricePerHotelNight);

        Assertions.assertThat(foundRoomAvailableResponseDto).isNotNull();
        Assertions.assertThat(foundRoomAvailableResponseDto.getRoomAvailabilityList().size()).isEqualTo(1);
    }

    @Test
    public void RoomService_GetRoomAvailableList_ReturnsRoomAvailableResponseDto_Case2() {
        when(roomRepository.findAllByRoomTypeIdIn(Mockito.anyList())).thenReturn(roomList);
        when(reservedRoomService.getReservedRoomListByReservationCheckInAndReservationCheckOut(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(reservedRoomList);
        when(roomTypeService.getRoomTypeIdList()).thenReturn(roomTypeIdList);
        when(roomTypeService.getRoomTypeById(Mockito.any(long.class))).thenReturn(roomType);

        RoomAvailableResponseDto foundRoomAvailableResponseDto = roomService.getRoomAvailableList(checkIn, checkOut, rooms, 2, 2, roomTypeIdList, 0);

        Assertions.assertThat(foundRoomAvailableResponseDto).isNotNull();
        Assertions.assertThat(foundRoomAvailableResponseDto.getRoomAvailabilityList().size()).isEqualTo(1);
    }

    @Test
    public void RoomService_GetRoomAvailableList_ReturnsRoomAvailableResponseDto_Case3() {
        when(roomRepository.findAllByRoomTypePricePerHotelNightLessThanEqual(Mockito.any(BigDecimal.class))).thenReturn(roomList);
        when(reservedRoomService.getReservedRoomListByReservationCheckInAndReservationCheckOut(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(reservedRoomList);
        when(roomTypeService.getRoomTypeIdList()).thenReturn(roomTypeIdList);
        when(roomTypeService.getRoomTypeById(Mockito.any(long.class))).thenReturn(roomType);

        RoomAvailableResponseDto foundRoomAvailableResponseDto = roomService.getRoomAvailableList(checkIn, checkOut, rooms, 2, 2, null, pricePerHotelNight);

        Assertions.assertThat(foundRoomAvailableResponseDto).isNotNull();
        Assertions.assertThat(foundRoomAvailableResponseDto.getRoomAvailabilityList().size()).isEqualTo(1);
    }

    @Test
    public void RoomService_GetRoomAvailableList_ReturnsRoomAvailableResponseDto_Case4() {
        when(roomRepository.findAll()).thenReturn(roomList);
        when(reservedRoomService.getReservedRoomListByReservationCheckInAndReservationCheckOut(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(reservedRoomList);
        when(roomTypeService.getRoomTypeIdList()).thenReturn(roomTypeIdList);
        when(roomTypeService.getRoomTypeById(Mockito.any(long.class))).thenReturn(roomType);

        RoomAvailableResponseDto foundRoomAvailableResponseDto = roomService.getRoomAvailableList(checkIn, checkOut, rooms, 2, 2, null, 0);

        Assertions.assertThat(foundRoomAvailableResponseDto).isNotNull();
        Assertions.assertThat(foundRoomAvailableResponseDto.getRoomAvailabilityList().size()).isEqualTo(1);
    }

    @Test
    public void RoomService_GetRoomAvailableList_ReturnsRoomList() {
        when(roomRepository.findAllByRoomTypeIdIn(Mockito.anyList())).thenReturn(roomList);
        when(reservedRoomService.getReservedRoomListByReservationCheckInAndReservationCheckOut(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(reservedRoomList);

        List<Room> foundRoomList = roomService.getRoomAvailableList(Date.valueOf(checkIn), Date.valueOf(checkOut), roomSelected);

        Assertions.assertThat(foundRoomList).isNotNull();
        Assertions.assertThat(foundRoomList.size()).isEqualTo(1);
    }

    @Test
    public void RoomService_GetRoomSelectedCostList_ReturnsRoomSelectedCostResponseDto() {
        when(roomTypeService.getRoomTypeById(Mockito.any(long.class))).thenReturn(roomType);

        RoomSelectedCostResponseDto foundSelectedCostResponseDto = roomService.getRoomSelectedCost(checkIn, checkOut, roomType.getId(), rooms);

        Assertions.assertThat(foundSelectedCostResponseDto).isNotNull();
        Assertions.assertThat(foundSelectedCostResponseDto.getRoomSelectedCost().getCost()).isEqualTo(BigDecimal.valueOf(500));
    }

    @Test
    public void RoomService_GetRoomFloorList_ReturnsIntegerList() {
        when(roomRepository.findAllFloors()).thenReturn(List.of(3, 2, 1));

        List<Integer> foundFloorList = roomService.getRoomFloorList();

        Assertions.assertThat(foundFloorList).isNotNull();
        Assertions.assertThat(foundFloorList.size()).isEqualTo(3);
    }

    @Test
    public void RoomService_GetRoomDetailsListByFloor_ReturnsRoomDetailsDtoList() {
        when(roomRepository.findAllByFloor(Mockito.any(int.class), Mockito.any(PageRequest.class))).thenReturn(new PageImpl<>(roomList));
        when(reservedRoomService.getReservedRoomListByFloor(Mockito.any(int.class))).thenReturn(reservedRoomList);

        List<RoomDetailsDto> foundRoomDetailsDtoList = roomService.getRoomDetailsListByFloor(1, 0, 10);

        Assertions.assertThat(foundRoomDetailsDtoList).isNotNull();
        Assertions.assertThat(foundRoomDetailsDtoList.size()).isEqualTo(2);
    }
}
