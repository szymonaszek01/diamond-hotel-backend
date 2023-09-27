package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.room.response.RoomAvailableResponseDto;
import com.app.diamondhotelbackend.dto.room.response.RoomSelectedCostResponseDto;
import com.app.diamondhotelbackend.exception.RoomProcessingException;
import com.app.diamondhotelbackend.service.room.RoomServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequestMapping("/api/v1/room")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://diamond-hotel-frontend-react.vercel.app/"}, allowCredentials = "true")
public class RoomController {

    private final RoomServiceImpl roomService;

    @GetMapping("/all/available")
    public ResponseEntity<RoomAvailableResponseDto> getRoomAvailabilityList(@RequestParam(value = "check-in") String checkIn,
                                                                            @RequestParam(value = "check-out") String checkOut,
                                                                            @RequestParam(value = "rooms") int rooms,
                                                                            @RequestParam(value = "adults") int adults,
                                                                            @RequestParam(value = "children") int children,
                                                                            @RequestParam(value = "room-type-id", required = false) List<Long> roomTypeIdList,
                                                                            @RequestParam(value = "price-per-hotel-night", defaultValue = "0", required = false) double pricePerHotelNight) {
        try {
            return ResponseEntity.ok(roomService.getRoomAvailableList(checkIn, checkOut, rooms, adults, children, roomTypeIdList, pricePerHotelNight));
        } catch (RoomProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/cost")
    public ResponseEntity<RoomSelectedCostResponseDto> getRoomSelectedCost(@RequestParam(value = "check-in") String checkIn,
                                                                           @RequestParam(value = "check-out") String checkOut,
                                                                           @RequestParam(value = "rooms") int rooms,
                                                                           @RequestParam(value = "room-type-id") long roomTypeId) {
        try {
            return ResponseEntity.ok(roomService.getRoomSelectedCost(checkIn, checkOut, roomTypeId, rooms));
        } catch (RoomProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
