package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.AvailableRoomTypeListRequestDto;
import com.app.diamondhotelbackend.dto.AvailableRoomTypeListResponseDto;
import com.app.diamondhotelbackend.dto.RoomTypeConfigurationInfoResponseDto;
import com.app.diamondhotelbackend.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/room-type")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://diamond-hotel-frontend.vercel.app", "http://localhost:4200"})
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @GetMapping("/configuration/info")
    public ResponseEntity<RoomTypeConfigurationInfoResponseDto> getRoomTypeConfigurationInfo() {
        return ResponseEntity.ok(roomTypeService.getRoomTypeConfigurationInfo());
    }

    @PostMapping("/available/info")
    public ResponseEntity<AvailableRoomTypeListResponseDto> getAvailableRoomTypeList(@RequestBody AvailableRoomTypeListRequestDto body) {
        AvailableRoomTypeListResponseDto availableRoomTypeListResponseDto = AvailableRoomTypeListResponseDto
                .builder()
                .availableRoomDtoList(roomTypeService.getAvailableRoomTypeList(body))
                .build();

        return ResponseEntity.ok(availableRoomTypeListResponseDto);
    }
}
