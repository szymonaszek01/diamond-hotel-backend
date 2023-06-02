package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.room.RoomDto;
import com.app.diamondhotelbackend.dto.shoppingcart.RoomTypeInfoDto;
import com.app.diamondhotelbackend.entity.Room;
import com.app.diamondhotelbackend.repository.ReservationRepository;
import com.app.diamondhotelbackend.repository.RoomRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;

    private final ReservationRepository reservationRepository;

    public List<Room> getRoomListByRoomTypeNameCheckInAndCheckOut(String roomTypeName, LocalDateTime checkIn, LocalDateTime checkOut) {
        return getRoomListByRoomTypeName(roomTypeName)
                .stream()
                .filter(room -> reservationRepository.countAllByRoomIdCheckInAndCheckOut(room.getId(), checkIn, checkOut) == 0)
                .collect(Collectors.toList());
    }

    public List<Room> getRoomListByRoomTypeName(String roomTypeName) {
        return roomRepository.findAllByRoomTypeName(roomTypeName);
    }

    public RoomDto toRoomDtoMapper(Room room) {
        return RoomDto.builder()
                .roomNumber(room.getNumber())
                .floor(room.getFloor())
                .build();
    }

    public boolean isMismatchBetweenSelectedAndAvailableRooms(List<RoomTypeInfoDto> roomTypeInfoDtoList, LocalDateTime checkIn, LocalDateTime checkOut) {
        return roomTypeInfoDtoList
                .stream()
                .anyMatch(roomTypeInfoDto -> roomTypeInfoDto.getSelectedRooms() > getRoomListByRoomTypeNameCheckInAndCheckOut(roomTypeInfoDto.getRoomTypeName(), checkIn, checkOut).size());
    }
}
