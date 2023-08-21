package com.app.diamondhotelbackend.service.roomtype;

import com.app.diamondhotelbackend.dto.roomtype.*;
import com.app.diamondhotelbackend.dto.shoppingcart.CarDto;
import com.app.diamondhotelbackend.dto.shoppingcart.CostSummaryDto;
import com.app.diamondhotelbackend.dto.shoppingcart.ShoppingCartSummaryRequestDto;
import com.app.diamondhotelbackend.dto.shoppingcart.ShoppingCartSummaryResponseDto;
import com.app.diamondhotelbackend.entity.RoomType;

import java.util.List;
import java.util.Optional;

public interface RoomTypeService {

    RoomTypeOfferDto getRoomTypeInfoList();

    RoomTypeConfigurationInfoResponseDto getRoomTypeConfigurationInfo();

    List<AvailableRoomTypeDto> getAvailableRoomTypeList(AvailableRoomTypeListRequestDto availableRoomTypeListRequestDto);

    ShoppingCartSummaryResponseDto getShoppingCartSummary(ShoppingCartSummaryRequestDto shoppingCartSummaryRequestDto);

    CostSummaryDto getShoppingCartSummaryCostWithCar(CarDto carDto);

    Optional<String> isValidRoomTypeName(String roomTypeName);

    Optional<Integer> isValidCapacity(String capacityAsString);

    RoomTypeDto toRoomTypeDtoMapper(RoomType roomType);
}
