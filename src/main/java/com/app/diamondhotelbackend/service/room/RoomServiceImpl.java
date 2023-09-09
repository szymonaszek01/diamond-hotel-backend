package com.app.diamondhotelbackend.service.room;

import com.app.diamondhotelbackend.dto.room.model.RoomAvailability;
import com.app.diamondhotelbackend.dto.room.response.RoomAvailableResponseDto;
import com.app.diamondhotelbackend.entity.Room;
import com.app.diamondhotelbackend.entity.RoomType;
import com.app.diamondhotelbackend.exception.RoomProcessingException;
import com.app.diamondhotelbackend.exception.RoomTypeProcessingException;
import com.app.diamondhotelbackend.repository.RoomRepository;
import com.app.diamondhotelbackend.service.reservedroom.ReservedRoomServiceImpl;
import com.app.diamondhotelbackend.service.roomtype.RoomTypeServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    private final RoomTypeServiceImpl roomTypeService;

    private final ReservedRoomServiceImpl reservedRoomService;

    @Override
    public RoomAvailableResponseDto getRoomAvailableList(String checkIn, String checkOut, int rooms, int adults, int children, List<Long> roomTypeIdList, double pricePerHotelNight) throws RoomProcessingException {
        if (checkIn == null || checkOut == null || checkIn.isEmpty() || checkOut.isEmpty() || rooms == 0 || adults == 0) {
            throw new RoomProcessingException(ConstantUtil.INVALID_PARAMETERS_EXCEPTION);
        }

        Date checkInAsDate = Date.valueOf(checkIn);
        Date checkOutAsDate = Date.valueOf(checkOut);

        List<Room> roomList = getRoomList(roomTypeIdList, pricePerHotelNight).stream()
                .filter(room -> !isRoomReserved(room.getId(), checkInAsDate, checkOutAsDate))
                .toList();

        List<RoomAvailability> roomAvailableList = toRoomAvailableListMapper(roomList);
        if (!isRoomAvailableListValid(roomAvailableList, rooms, adults, children)) {
            throw new RoomProcessingException(ConstantUtil.AVAILABLE_ROOM_NOT_FOUND);
        }

        return RoomAvailableResponseDto.builder()
                .checkIn(checkInAsDate)
                .checkOut(checkOutAsDate)
                .roomAvailabilityList(roomAvailableList)
                .build();
    }

    private List<RoomAvailability> toRoomAvailableListMapper(List<Room> roomList) {
        return roomTypeService.getRoomTypeIdList().stream()
                .map(roomTypeId -> getRoomAvailable(roomList, roomTypeId))
                .toList();
    }

    private RoomAvailability getRoomAvailable
            (List<Room> roomList, long roomTypeId) {
        return RoomAvailability.builder()
                .roomTypeId(roomTypeId)
                .availability(roomList.stream()
                        .filter(room -> room.getRoomType().getId() == roomTypeId)
                        .toList()
                        .size()
                ).build();
    }

    private List<Room> getRoomList(List<Long> roomTypeIdList, double pricePerHotelNight) {
        if (roomTypeIdList != null && pricePerHotelNight > 0) {
            return roomRepository.findAllByRoomTypeIdInAndRoomTypePricePerHotelNightLessThanEqual(roomTypeIdList, BigDecimal.valueOf(pricePerHotelNight));
        } else if (roomTypeIdList != null) {
            return roomRepository.findAllByRoomTypeIdIn(roomTypeIdList);
        } else if (pricePerHotelNight > 0) {
            return roomRepository.findAllByRoomTypePricePerHotelNightLessThanEqual(BigDecimal.valueOf(pricePerHotelNight));
        } else {
            return roomRepository.findAll();
        }
    }

    private boolean isRoomAvailableListValid(List<RoomAvailability> roomAvailabilityList, int rooms, int adults, int children) {
        try {
            int roomsFromRoomAvailabilityList = 0;
            int adultsFromRoomAvailabilityList = 0;
            int childrenFromRoomAvailabilityList = 0;

            for (RoomAvailability roomAvailability : roomAvailabilityList) {
                RoomType roomType = roomTypeService.getRoomTypeById(roomAvailability.getRoomTypeId());
                roomsFromRoomAvailabilityList += roomAvailability.getAvailability();
                adultsFromRoomAvailabilityList += roomType.getAdults() * roomAvailability.getAvailability();
                childrenFromRoomAvailabilityList += roomType.getChildren() * roomAvailability.getAvailability();
            }

            return roomsFromRoomAvailabilityList >= rooms && adultsFromRoomAvailabilityList >= adults && childrenFromRoomAvailabilityList >= children;

        } catch (RoomTypeProcessingException e) {
            return false;
        }
    }

    private boolean isRoomReserved(long roomId, Date checkIn, Date checkOut) {
        return reservedRoomService.getReservedRoomListByReservationCheckInAndReservationCheckOut(checkIn, checkOut).stream()
                .anyMatch(reservedRoom -> reservedRoom.getRoom().getId() == roomId);
    }
}
