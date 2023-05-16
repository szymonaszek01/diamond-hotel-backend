package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.RoomTypeConfigurationInfoResponseDto;
import com.app.diamondhotelbackend.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
