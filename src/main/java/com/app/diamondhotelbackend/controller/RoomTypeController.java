package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.roomtype.AvailableRoomTypeListRequestDto;
import com.app.diamondhotelbackend.dto.roomtype.AvailableRoomTypeListResponseDto;
import com.app.diamondhotelbackend.dto.roomtype.RoomTypeConfigurationInfoResponseDto;
import com.app.diamondhotelbackend.dto.roomtype.RoomTypeOfferDto;
import com.app.diamondhotelbackend.dto.shoppingcart.CarDto;
import com.app.diamondhotelbackend.dto.shoppingcart.CostSummaryDto;
import com.app.diamondhotelbackend.dto.shoppingcart.ShoppingCartSummaryRequestDto;
import com.app.diamondhotelbackend.dto.shoppingcart.ShoppingCartSummaryResponseDto;
import com.app.diamondhotelbackend.exception.CheckInOutFormatException;
import com.app.diamondhotelbackend.exception.NotAllSelectedRoomsAvailableException;
import com.app.diamondhotelbackend.service.roomtype.RoomTypeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/api/v1/room-type")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://diamond-hotel-frontend.vercel.app", "http://localhost:4200", "http://localhost:3000"}, allowCredentials = "true")
public class RoomTypeController {

    private final RoomTypeServiceImpl roomTypeService;

    @GetMapping("/all/info")
    public ResponseEntity<RoomTypeOfferDto> getRoomTypeInfoList() {
        return ResponseEntity.ok(roomTypeService.getRoomTypeInfoList());
    }

    @GetMapping("/configuration/info")
    public ResponseEntity<RoomTypeConfigurationInfoResponseDto> getRoomTypeConfigurationInfo() {
        return ResponseEntity.ok(roomTypeService.getRoomTypeConfigurationInfo());
    }

    @PostMapping("/available/info")
    public ResponseEntity<AvailableRoomTypeListResponseDto> getAvailableRoomTypeList(@RequestBody AvailableRoomTypeListRequestDto body) {
        AvailableRoomTypeListResponseDto availableRoomTypeListResponseDto = AvailableRoomTypeListResponseDto.builder().availableRoomDtoList(roomTypeService.getAvailableRoomTypeList(body)).build();

        return ResponseEntity.ok(availableRoomTypeListResponseDto);
    }

    @PostMapping("/summary/shopping/cart")
    public ResponseEntity<ShoppingCartSummaryResponseDto> getShoppingCartSummary(@RequestBody ShoppingCartSummaryRequestDto shoppingCartSummaryRequestDto) {
        try {
            ShoppingCartSummaryResponseDto result = roomTypeService.getShoppingCartSummary(shoppingCartSummaryRequestDto);
            return ResponseEntity.ok(result);

        } catch (CheckInOutFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (NotAllSelectedRoomsAvailableException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/summary/shopping/cart/cost/with/car")
    public ResponseEntity<CostSummaryDto> getShoppingCartSummaryCostWithCar(@RequestBody CarDto carDto) {
        try {
            CostSummaryDto result = roomTypeService.getShoppingCartSummaryCostWithCar(carDto);
            return ResponseEntity.ok(result);

        } catch (CheckInOutFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
