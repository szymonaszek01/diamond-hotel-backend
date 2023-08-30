package com.app.diamondhotelbackend.service.roomtype;

import com.app.diamondhotelbackend.dto.roomtype.*;
import com.app.diamondhotelbackend.dto.shoppingcart.*;
import com.app.diamondhotelbackend.entity.RoomType;
import com.app.diamondhotelbackend.exception.CheckInOutFormatException;
import com.app.diamondhotelbackend.exception.NotAllSelectedRoomsAvailableException;
import com.app.diamondhotelbackend.repository.RoomTypeRepository;
import com.app.diamondhotelbackend.service.roomtypeopinion.RoomTypeOpinionServiceImpl;
import com.app.diamondhotelbackend.service.room.RoomServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    private final RoomServiceImpl roomService;

    private final RoomTypeOpinionServiceImpl roomTypeOpinionService;

    @Override
    public RoomTypeOfferDto getRoomTypeInfoList() {
        return RoomTypeOfferDto.builder().roomTypeDtoList(roomTypeRepository.findAll()
                .stream()
                .map(this::toRoomTypeDtoMapper)
                .collect(Collectors.toList())
        ).build();
    }

    @Override
    public RoomTypeConfigurationInfoResponseDto getRoomTypeConfigurationInfo() {
        List<String> roomTypeList = roomTypeRepository.findAllNameList();
        List<String> capacityList = roomTypeRepository.findAllCapacityList();

        return RoomTypeConfigurationInfoResponseDto.builder().roomTypeList(roomTypeList).capacityList(capacityList).build();
    }

    @Override
    public List<AvailableRoomTypeDto> getAvailableRoomTypeList(AvailableRoomTypeListRequestDto availableRoomTypeListRequestDto) {
        Optional<LocalDateTime> checkIn = DateUtil.isValidCheckInOrCheckOut(availableRoomTypeListRequestDto.getCheckIn());
        Optional<LocalDateTime> checkOut = DateUtil.isValidCheckInOrCheckOut(availableRoomTypeListRequestDto.getCheckOut());
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

    @Override
    public ShoppingCartSummaryResponseDto getShoppingCartSummary(ShoppingCartSummaryRequestDto shoppingCartSummaryRequestDto) {
        Optional<LocalDateTime> checkIn = DateUtil.isValidCheckInOrCheckOut(shoppingCartSummaryRequestDto.getCheckIn());
        Optional<LocalDateTime> checkOut = DateUtil.isValidCheckInOrCheckOut(shoppingCartSummaryRequestDto.getCheckOut());
        if (checkIn.isEmpty() || checkOut.isEmpty()) {
            throw new CheckInOutFormatException(ConstantUtil.INCORRECT_CHECK_IN_OR_CHECK_OUT_FORMAT_EXCEPTION);
        }
        if (roomService.isMismatchBetweenSelectedAndAvailableRooms(shoppingCartSummaryRequestDto.getRoomTypeInfo(), checkIn.get(), checkOut.get())) {
            throw new NotAllSelectedRoomsAvailableException(ConstantUtil.NUMBER_OF_AVAILABLE_ROOMS_HAS_CHANGED_EXCEPTION);
        }

        long totalRoomCost = 0;
        long duration = DateUtil.getDuration(checkIn.get(), checkOut.get());
        List<RoomTypeSummaryDto> roomTypeSummaryDtoList = getRoomTypeSummaryDtoList(shoppingCartSummaryRequestDto.getRoomTypeInfo(), duration);
        for (RoomTypeSummaryDto roomTypeSummaryDto : roomTypeSummaryDtoList) {
            totalRoomCost += roomTypeSummaryDto.getSelectedRoomsCost().longValue();
        }
        CostSummaryDto costSummaryDto = getCostSummaryDto(totalRoomCost, duration, shoppingCartSummaryRequestDto.isCarRent(), shoppingCartSummaryRequestDto.isCarPickUp());

        return ShoppingCartSummaryResponseDto.builder()
                .checkIn(checkIn.get().toString())
                .checkOut(checkOut.get().toString())
                .costSummaryDto(costSummaryDto)
                .roomTypeSummary(roomTypeSummaryDtoList)
                .build();
    }

    @Override
    public CostSummaryDto getShoppingCartSummaryCostWithCar(CarDto carDto) {
        Optional<LocalDateTime> checkIn = DateUtil.isValidCheckInOrCheckOut(carDto.getCheckIn());
        Optional<LocalDateTime> checkOut = DateUtil.isValidCheckInOrCheckOut(carDto.getCheckOut());
        if (checkIn.isEmpty() || checkOut.isEmpty()) {
            throw new CheckInOutFormatException(ConstantUtil.INCORRECT_CHECK_IN_OR_CHECK_OUT_FORMAT_EXCEPTION);
        }

        return getCostSummaryDto(carDto.getTotalRoomCost().longValue(), carDto.getCarRentDuration(), carDto.isCarRent(), carDto.isCarPickUp());
    }

    @Override
    public Optional<String> isValidRoomTypeName(String roomTypeName) {
        if (roomTypeName == null || roomTypeName.isEmpty() || !roomTypeRepository.findAllNameList().contains(roomTypeName)) {
            return Optional.empty();
        }

        return Optional.of(roomTypeName);
    }

    @Override
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

    @Override
    public RoomTypeDto toRoomTypeDtoMapper(RoomType roomType) {
        return RoomTypeDto.builder()
                .id(roomType.getId())
                .name(roomType.getName())
                .capacity(roomType.getCapacity())
                .pricePerHotelNight(roomType.getPricePerHotelNight())
                .equipmentList(roomType.getEquipmentList())
                .image(roomType.getImage())
                .opinion(roomTypeOpinionService.getRoomTypeOpinionSummaryDto(roomType.getName()))
                .build();
    }

    private List<AvailableRoomTypeDto> getAvailableRoomTypeDtoListByCheckInAndCheckOut(LocalDateTime checkIn, LocalDateTime checkOut) {
        return roomTypeRepository.findAll()
                .stream()
                .map(roomType -> toAvailableRoomTypeDtoMapper(roomType, checkIn, checkOut))
                .toList();
    }

    private List<RoomTypeSummaryDto> getRoomTypeSummaryDtoList(List<RoomTypeInfoDto> roomTypeInfoDtoList, long duration) {
        return roomTypeInfoDtoList
                .stream()
                .map(roomTypeInfoDto -> toRoomTypeSummaryDtoMapper(roomTypeInfoDto, duration))
                .toList();
    }

    private CostSummaryDto getCostSummaryDto(long totalRoomCost, long duration, boolean isCarRent, boolean isCarPickUp) {
        long carRent = isCarRent ? 50 * duration : 0;
        long carPickUp = isCarPickUp ? 50 : 0;

        return CostSummaryDto.builder()
                .totalWithoutTax(BigDecimal.valueOf(totalRoomCost))
                .carRent(BigDecimal.valueOf(carRent))
                .carPickUp(BigDecimal.valueOf(carPickUp))
                .tax(BigDecimal.valueOf(0.1 * totalRoomCost))
                .build();
    }

    private AvailableRoomTypeDto toAvailableRoomTypeDtoMapper(RoomType roomType, LocalDateTime checkIn, LocalDateTime checkOut) {
        return AvailableRoomTypeDto.builder()
                .id(roomType.getId())
                .name(roomType.getName())
                .capacity(roomType.getCapacity())
                .pricePerHotelNight(roomType.getPricePerHotelNight())
                .equipmentList(roomType.getEquipmentList())
                .image(roomType.getImage())
                .opinion(roomTypeOpinionService.getRoomTypeOpinionSummaryDto(roomType.getName()))
                .available(roomService.getRoomListByRoomTypeNameCheckInAndCheckOut(roomType.getName(), checkIn, checkOut).size())
                .build();
    }

    private RoomTypeSummaryDto toRoomTypeSummaryDtoMapper(RoomTypeInfoDto roomTypeInfoDto, long duration) {
        BigDecimal pricePerHotelNight = roomTypeRepository.findPricePerHotelNightByRoomTypeName(roomTypeInfoDto.getRoomTypeName()).orElse(BigDecimal.valueOf(0));
        int capacity = roomTypeRepository.findCapacityByRoomTypeName(roomTypeInfoDto.getRoomTypeName()).orElse(0);

        return RoomTypeSummaryDto.builder()
                .pricePerHotelNight(pricePerHotelNight)
                .capacity(capacity)
                .selectedRoomsCost(BigDecimal.valueOf(duration * pricePerHotelNight.longValue() * roomTypeInfoDto.getSelectedRooms()))
                .roomTypeInfo(roomTypeInfoDto)
                .build();
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
