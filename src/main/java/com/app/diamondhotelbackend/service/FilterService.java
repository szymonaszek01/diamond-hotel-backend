package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.shoppingcart.RoomTypeInfoDto;
import com.app.diamondhotelbackend.entity.Room;
import com.app.diamondhotelbackend.repository.ReservationRepository;
import com.app.diamondhotelbackend.repository.RoomRepository;
import com.app.diamondhotelbackend.repository.RoomTypeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class FilterService {

    private final RoomTypeRepository roomTypeRepository;

    private final RoomRepository roomRepository;

    private final ReservationRepository reservationRepository;

    public Optional<LocalDateTime> isValidCheckInOrCheckOut(String localDateTimeAsString) {
        try {
            if (localDateTimeAsString == null || localDateTimeAsString.isEmpty()) {
                return Optional.empty();
            }
            LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeAsString, DateTimeFormatter.ISO_DATE_TIME);
            return Optional.of(localDateTime);

        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public Optional<String> isValidRoomTypeName(String roomTypeName) {
        if (roomTypeName == null || roomTypeName.isEmpty() || !roomTypeRepository.findAllNameList().contains(roomTypeName)) {
            return Optional.empty();
        }

        return Optional.of(roomTypeName);
    }

    public Optional<Integer> isValidCapacity(String capacityAsString) {
        try {
            if (capacityAsString == null || capacityAsString.isEmpty() || !roomTypeRepository.findAllCapacityList().contains(capacityAsString)) {
                return Optional.empty();
            }

            Integer capacity = Integer.parseInt(capacityAsString);
            return Optional.of(capacity);

        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public boolean isMismatchBetweenSelectedAndAvailableRooms(List<RoomTypeInfoDto> roomTypeInfoDtoList, LocalDateTime checkIn, LocalDateTime checkOut) {
        List<Room> roomList = roomRepository.findAll()
                .stream()
                .filter(room -> reservationRepository.countAllByRoomIdAndOccupyStatus(room.getId(), checkIn, checkOut) == 0)
                .toList();

        return roomTypeInfoDtoList
                .stream()
                .anyMatch(roomTypeInfoDto -> roomTypeInfoDto.getSelectedRooms() > countAvailableRoomsByRoomTypeName(roomTypeInfoDto.getRoomTypeName(), roomList));
    }

    public int countAvailableRoomsByRoomTypeId(long roomTypeId, List<Room> roomList) {
        return roomList
                .stream()
                .filter(room -> room.getRoomType().getId() == roomTypeId)
                .toList()
                .size();
    }

    public int countAvailableRoomsByRoomTypeName(String roomTypeName, List<Room> roomList) {
        return roomList
                .stream()
                .filter(room -> room.getRoomType().getName().equals(roomTypeName))
                .toList()
                .size();
    }
}
