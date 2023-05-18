package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.AvailableRoomTypeDto;
import com.app.diamondhotelbackend.dto.AvailableRoomTypeListRequestDto;
import com.app.diamondhotelbackend.dto.RoomTypeConfigurationInfoResponseDto;
import com.app.diamondhotelbackend.entity.Room;
import com.app.diamondhotelbackend.entity.RoomType;
import com.app.diamondhotelbackend.repository.ReservationRepository;
import com.app.diamondhotelbackend.repository.RoomRepository;
import com.app.diamondhotelbackend.repository.RoomTypeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    private final RoomRepository roomRepository;

    private final ReservationRepository reservationRepository;

    private final RoomTypeOpinionService roomTypeOpinionService;

    public RoomTypeConfigurationInfoResponseDto getRoomTypeConfigurationInfo() {
        List<String> roomTypeList = roomTypeRepository.findAllNameList();
        List<String> capacityList = roomTypeRepository.findAllCapacityList();

        return RoomTypeConfigurationInfoResponseDto.builder().roomTypeList(roomTypeList).capacityList(capacityList).build();
    }

    public List<AvailableRoomTypeDto> getAvailableRoomTypeList(AvailableRoomTypeListRequestDto availableRoomTypeListRequestDto) {
        Optional<LocalDateTime> checkIn = isValidCheckInOrCheckOut(availableRoomTypeListRequestDto.getCheckIn());
        Optional<LocalDateTime> checkOut = isValidCheckInOrCheckOut(availableRoomTypeListRequestDto.getCheckOut());
        Optional<String> roomTypeName = isValidRoomTypeName(availableRoomTypeListRequestDto.getRoomTypeName());
        Optional<Integer> capacity = isValidCapacity(availableRoomTypeListRequestDto.getCapacity());
        if (checkIn.isEmpty() || checkOut.isEmpty()) {
            return Collections.emptyList();
        }

        List<AvailableRoomTypeDto> availableRoomTypeDtoList = getAvailableRoomTypeDtoListByCheckInAndCheckOut(checkIn.get(), checkOut.get());
        if (roomTypeName.isEmpty() && capacity.isEmpty()) {
            return availableRoomTypeDtoList;
        } else if (roomTypeName.isPresent() && capacity.isEmpty()) {
            return filterAvailableRoomTypeDtoByRoomTypeName(availableRoomTypeDtoList, roomTypeName.get());
        } else if (roomTypeName.isEmpty()) {
            return filterAvailableRoomTypeDtoByCapacity(availableRoomTypeDtoList, capacity.get());
        } else {
            availableRoomTypeDtoList = filterAvailableRoomTypeDtoByRoomTypeName(availableRoomTypeDtoList, roomTypeName.get());
            return filterAvailableRoomTypeDtoByCapacity(availableRoomTypeDtoList, capacity.get());
        }
    }

    private Optional<LocalDateTime> isValidCheckInOrCheckOut(String localDateTimeAsString) {
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

    private Optional<String> isValidRoomTypeName(String roomTypeName) {
        if (roomTypeName == null || roomTypeName.isEmpty() || !roomTypeRepository.findAllNameList().contains(roomTypeName)) {
            return Optional.empty();
        }

        return Optional.of(roomTypeName);
    }

    private Optional<Integer> isValidCapacity(String capacityAsString) {
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

    private List<AvailableRoomTypeDto> getAvailableRoomTypeDtoListByCheckInAndCheckOut(LocalDateTime checkIn, LocalDateTime checkOut) {
        List<Room> roomList = roomRepository.findAll()
                .stream()
                .filter(room -> reservationRepository.countAllByRoomIdAndOccupyStatus(room.getId(), checkIn, checkOut) == 0)
                .toList();

        return roomTypeRepository.findAll()
                .stream()
                .map(roomType -> toAvailableRoomTypeDtoMapper(roomType, roomList))
                .toList();
    }

    private AvailableRoomTypeDto toAvailableRoomTypeDtoMapper(RoomType roomType, List<Room> roomList) {
        return AvailableRoomTypeDto.builder()
                .id(roomType.getId())
                .name(roomType.getName())
                .capacity(roomType.getCapacity())
                .pricePerHotelNight(roomType.getPricePerHotelNight())
                .equipmentList(roomType.getEquipmentList())
                .image(roomType.getImage())
                .opinion(roomTypeOpinionService.getRoomTypeOpinionSummaryDto(roomType.getName()))
                .available(countAvailableRoomsByRoomTypeId(roomType.getId(), roomList))
                .build();
    }

    private int countAvailableRoomsByRoomTypeId(long roomTypeId, List<Room> roomList) {
        return roomList
                .stream()
                .filter(room -> room.getRoomType().getId() == roomTypeId)
                .toList()
                .size();
    }

    private List<AvailableRoomTypeDto> filterAvailableRoomTypeDtoByRoomTypeName(List<AvailableRoomTypeDto> availableRoomTypeDtoList, String roomTypeName) {
        return availableRoomTypeDtoList
                .stream()
                .filter(availableRoomTypeDto -> roomTypeName.equals(availableRoomTypeDto.getName()))
                .toList();
    }

    private List<AvailableRoomTypeDto> filterAvailableRoomTypeDtoByCapacity(List<AvailableRoomTypeDto> availableRoomTypeDtoList, int capacity) {
        return availableRoomTypeDtoList
                .stream()
                .filter(availableRoomTypeDto -> capacity == availableRoomTypeDto.getCapacity())
                .toList();
    }
}
