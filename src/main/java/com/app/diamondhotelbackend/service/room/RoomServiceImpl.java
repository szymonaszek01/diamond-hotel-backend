package com.app.diamondhotelbackend.service.room;

import com.app.diamondhotelbackend.dto.room.model.RoomAvailability;
import com.app.diamondhotelbackend.dto.room.model.RoomSelected;
import com.app.diamondhotelbackend.dto.room.model.RoomSelectedCost;
import com.app.diamondhotelbackend.dto.room.request.AddRoomRequestDto;
import com.app.diamondhotelbackend.dto.room.response.RoomAvailableResponseDto;
import com.app.diamondhotelbackend.dto.room.response.RoomDetailsDto;
import com.app.diamondhotelbackend.dto.room.response.RoomSelectedCostResponseDto;
import com.app.diamondhotelbackend.entity.ReservedRoom;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;
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
    public Room createRoom(AddRoomRequestDto addRoomRequestDto) {
        if (addRoomRequestDto == null || addRoomRequestDto.getNumber() == 0 || addRoomRequestDto.getFloor() == 0) {
            throw new RoomProcessingException(ConstantUtil.INVALID_PARAMETERS_EXCEPTION);
        }

        Room room;
        RoomType roomType = null;
        try {
            roomType = roomTypeService.getRoomTypeById(addRoomRequestDto.getRoomTypeId());
            room = getRoomByNumberAndFloor(addRoomRequestDto.getNumber(), addRoomRequestDto.getFloor());
            room.setRoomType(roomType);

        } catch (RoomTypeProcessingException e) {
            throw new RoomProcessingException(ConstantUtil.INVALID_PARAMETERS_EXCEPTION);
        } catch (RoomProcessingException e) {
            room = Room.builder().number(addRoomRequestDto.getNumber()).floor(addRoomRequestDto.getFloor()).roomType(roomType).build();
        }

        return roomRepository.save(room);
    }

    @Override
    public RoomAvailableResponseDto getRoomAvailableList(String checkIn, String checkOut, int rooms, int adults, int children, List<Long> roomTypeIdList, double pricePerHotelNight) throws RoomProcessingException {
        Optional<Date> checkInAsDate = DateUtil.parseDate(checkIn);
        Optional<Date> checkOutAsDate = DateUtil.parseDate(checkOut);

        if (checkInAsDate.isEmpty() || checkInAsDate.get().before(new Date(System.currentTimeMillis())) || checkOutAsDate.isEmpty() || checkOutAsDate.get().before(new Date(System.currentTimeMillis())) || rooms == 0 || adults == 0) {
            throw new RoomProcessingException(ConstantUtil.INVALID_PARAMETERS_EXCEPTION);
        }

        List<Room> roomList = getNotReservedRoomList(checkInAsDate.get(), checkOutAsDate.get(), roomTypeIdList, pricePerHotelNight);
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
        List<Room> roomList = getNotReservedRoomList(checkIn, checkOut, List.of(roomSelected.getRoomTypeId()), 0);
        if (roomList.size() < roomSelected.getRooms()) {
            throw new RoomProcessingException(ConstantUtil.NOT_ENOUGH_AVAILABLE_ROOMS);
        }

        return roomList.stream()
                .limit(roomSelected.getRooms())
                .collect(Collectors.toList());
    }

    @Override
    public List<Integer> getRoomFloorList() {
        return roomRepository.findAllFloors();
    }

    @Override
    public List<RoomDetailsDto> getRoomDetailsListByFloor(int floor, int page, int size) {
        if (floor < 1 || page < 0 || size < 1) {
            return Collections.emptyList();
        }

        List<Sort.Order> orderList = List.of(new Sort.Order(Sort.Direction.ASC, "number"));
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderList));
        List<Room> roomList = roomRepository.findAllByFloor(floor, pageable);
        List<ReservedRoom> reservedRoomList = reservedRoomService.getReservedRoomListByFloor(floor);

        List<RoomDetailsDto> roomDetailsDtoListWithoutReservations = roomList.stream()
                .map(this::toRoomDetailsDtoMapper)
                .toList();

        List<RoomDetailsDto> roomDetailsDtoListWithReservations = reservedRoomList.stream()
                .map(this::toRoomDetailsDtoMapper)
                .toList();

        return roomDetailsDtoListWithoutReservations.stream()
                .map(roomDetailsDto -> getRoomDetailsDtoByNumber(roomDetailsDto.getNumber(), roomDetailsDtoListWithReservations).orElse(roomDetailsDto))
                .toList();
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

    @Override
    public Room getRoomByNumberAndFloor(int number, int floor) {
        return roomRepository.findByNumberAndFloor(number, floor).orElseThrow(() -> new RoomProcessingException(ConstantUtil.ROOM_NOT_FOUND));
    }

    private Optional<RoomDetailsDto> getRoomDetailsDtoByNumber(int number, List<RoomDetailsDto> roomDetailsDtoList) {
        return roomDetailsDtoList.stream().filter(roomDetailsDto -> roomDetailsDto.getNumber() == number).findAny();
    }

    private RoomDetailsDto toRoomDetailsDtoMapper(Room room) {
        return RoomDetailsDto.builder()
                .number(room.getNumber())
                .floor(room.getFloor())
                .roomTypeName(room.getRoomType().getName())
                .roomTypeShortcut(toRoomTypeNameShortcutMapper(room.getRoomType().getName()))
                .build();
    }

    private RoomDetailsDto toRoomDetailsDtoMapper(ReservedRoom reservedRoom) {
        return RoomDetailsDto.builder()
                .number(reservedRoom.getRoom().getNumber())
                .floor(reservedRoom.getRoom().getFloor())
                .roomTypeName(reservedRoom.getRoom().getRoomType().getName())
                .roomTypeShortcut(toRoomTypeNameShortcutMapper(reservedRoom.getRoom().getRoomType().getName()))
                .occupied(reservedRoom.getOccupied() == 1)
                .reservationId(reservedRoom.getReservation().getId())
                .checkIn(reservedRoom.getReservation().getCheckIn().toString())
                .checkOut(reservedRoom.getReservation().getCheckOut().toString())
                .email(reservedRoom.getReservation().getUserProfile().getEmail())
                .build();
    }

    private String toRoomTypeNameShortcutMapper(String roomTypeName) {
        return Arrays.stream(roomTypeName.split(" "))
                .map(word -> word.toUpperCase().substring(0, 1))
                .collect(Collectors.joining());
    }

    private List<RoomAvailability> toRoomAvailableListMapper(List<Room> roomList) {
        return roomTypeService.getRoomTypeIdList().stream()
                .map(roomTypeId -> getRoomAvailable(roomList, roomTypeId))
                .toList();
    }

    private RoomAvailability getRoomAvailable(List<Room> roomList, long roomTypeId) {
        return RoomAvailability.builder()
                .roomTypeId(roomTypeId)
                .availability(roomList.stream()
                        .filter(room -> room.getRoomType().getId() == roomTypeId)
                        .toList()
                        .size()
                ).build();
    }

    private List<Room> getNotReservedRoomList(Date checkIn, Date checkOut, List<Long> roomTypeIdList, double pricePerHotelNight) {
        List<Room> reservedRoomList = reservedRoomService.getReservedRoomListByReservationCheckInAndReservationCheckOut(checkIn, checkOut)
                .stream()
                .map(ReservedRoom::getRoom)
                .toList();

        return getRoomList(roomTypeIdList, pricePerHotelNight).stream()
                .filter(room -> reservedRoomList.stream().noneMatch(reservedRoom -> reservedRoom.getId() == room.getId()))
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
}
