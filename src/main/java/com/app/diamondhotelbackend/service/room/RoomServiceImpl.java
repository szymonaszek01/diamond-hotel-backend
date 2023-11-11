package com.app.diamondhotelbackend.service.room;

import com.app.diamondhotelbackend.dto.room.model.RoomAvailability;
import com.app.diamondhotelbackend.dto.room.model.RoomSelected;
import com.app.diamondhotelbackend.dto.room.model.RoomSelectedCost;
import com.app.diamondhotelbackend.dto.room.response.RoomAvailableResponseDto;
import com.app.diamondhotelbackend.dto.room.response.RoomSelectedCostResponseDto;
import com.app.diamondhotelbackend.entity.Room;
import com.app.diamondhotelbackend.entity.RoomType;
import com.app.diamondhotelbackend.exception.RoomProcessingException;
import com.app.diamondhotelbackend.exception.RoomTypeProcessingException;
import com.app.diamondhotelbackend.repository.RoomRepository;
import com.app.diamondhotelbackend.service.reservedroom.ReservedRoomServiceImpl;
import com.app.diamondhotelbackend.service.roomtype.RoomTypeServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    private final RoomTypeServiceImpl roomTypeService;

    private final ReservedRoomServiceImpl reservedRoomService;

    @Override
    public RoomAvailableResponseDto getRoomAvailableList(String checkIn, String checkOut, int rooms, int adults, int children, List<Long> roomTypeIdList, double pricePerHotelNight) throws RoomProcessingException {
        Optional<Date> checkInAsDate = DateUtil.parseDate(checkIn);
        Optional<Date> checkOutAsDate = DateUtil.parseDate(checkOut);

        if (checkInAsDate.isEmpty() || checkInAsDate.get().before(new Date(System.currentTimeMillis())) || checkOutAsDate.isEmpty() || checkOutAsDate.get().before(new Date(System.currentTimeMillis())) || rooms == 0 || adults == 0) {
            throw new RoomProcessingException(ConstantUtil.INVALID_PARAMETERS_EXCEPTION);
        }

        List<Room> roomList = filterRoomList(checkInAsDate.get(), checkOutAsDate.get(), roomTypeIdList, pricePerHotelNight);
        List<RoomAvailability> roomAvailableList = toRoomAvailableListMapper(roomList);
        if (!isRoomAvailableListValid(roomAvailableList, rooms, adults, children)) {
            throw new RoomProcessingException(ConstantUtil.AVAILABLE_ROOM_NOT_FOUND_EXCEPTION);
        }

        return RoomAvailableResponseDto.builder()
                .checkIn(checkInAsDate.get())
                .checkOut(checkOutAsDate.get())
                .roomAvailabilityList(roomAvailableList)
                .build();
    }

    @Override
    public List<Room> getRoomAvailableList(Date checkIn, Date checkOut, RoomSelected roomSelected) throws RoomProcessingException {
        List<Room> roomList = filterRoomList(checkIn, checkOut, List.of(roomSelected.getRoomTypeId()), 0);
        if (roomList.size() < roomSelected.getRooms()) {
            throw new RoomProcessingException(ConstantUtil.NOT_ENOUGH_AVAILABLE_ROOMS);
        }

        return roomList.stream()
                .limit(roomSelected.getRooms())
                .collect(Collectors.toList());
    }

    @Override
    public RoomSelectedCostResponseDto getRoomSelectedCost(String checkIn, String checkOut, long roomTypeId, int rooms) throws RoomTypeProcessingException {
        Optional<Date> checkInAsDate = DateUtil.parseDate(checkIn);
        Optional<Date> checkOutAsDate = DateUtil.parseDate(checkOut);

        if (checkInAsDate.isEmpty() || checkOutAsDate.isEmpty()) {
            throw new RoomProcessingException(ConstantUtil.INVALID_PARAMETERS_EXCEPTION);
        }

        RoomType roomType = roomTypeService.getRoomTypeById(roomTypeId);
        RoomSelectedCost roomSelectedCost = RoomSelectedCost.builder()
                .roomTypeId(roomTypeId)
                .name(roomType.getName())
                .rooms(rooms)
                .cost(getCost(checkInAsDate.get(), checkOutAsDate.get(), roomType.getPricePerHotelNight(), rooms))
                .build();

        return RoomSelectedCostResponseDto.builder()
                .checkIn(checkInAsDate.get())
                .checkOut(checkOutAsDate.get())
                .roomSelectedCost(roomSelectedCost)
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

    private List<Room> filterRoomList(Date checkIn, Date checkOut, List<Long> roomTypeIdList, double pricePerHotelNight) {
        return getRoomList(roomTypeIdList, pricePerHotelNight).stream()
                .filter(room -> !isRoomReserved(room.getId(), checkIn, checkOut))
                .toList();
    }

    private BigDecimal getCost(Date checkIn, Date checkOut, BigDecimal pricePerHotelNight, int rooms) {
        long duration = DateUtil.getDifferenceBetweenTwoDates(checkIn, checkOut);
        long cost = pricePerHotelNight.longValue() * duration * rooms;

        return BigDecimal.valueOf(cost);
    }

    private List<Room> getRoomList(List<Long> roomTypeIdList, double pricePerHotelNight) {
        if (roomTypeIdList != null && roomTypeIdList.size() > 0 && pricePerHotelNight > 0) {
            return roomRepository.findAllByRoomTypeIdInAndRoomTypePricePerHotelNightLessThanEqual(roomTypeIdList, BigDecimal.valueOf(pricePerHotelNight));
        } else if (roomTypeIdList != null && roomTypeIdList.size() > 0) {
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
