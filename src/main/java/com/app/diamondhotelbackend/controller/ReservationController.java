package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.auth.RegisterRequestDto;
import com.app.diamondhotelbackend.dto.auth.UserProfileDetailsResponseDto;
import com.app.diamondhotelbackend.dto.reservation.UserReservationAllRequestDto;
import com.app.diamondhotelbackend.dto.reservation.UserReservationAllResponseDto;
import com.app.diamondhotelbackend.dto.shoppingcart.CostSummaryDto;
import com.app.diamondhotelbackend.exception.CheckInOutFormatException;
import com.app.diamondhotelbackend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/api/v1/reservation")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://diamond-hotel-frontend.vercel.app", "http://localhost:4200"})
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/all/info")
    public ResponseEntity<UserReservationAllResponseDto> getUserReservationInfoList(@RequestBody UserReservationAllRequestDto userReservationAllRequestDto) {
        try {
            return ResponseEntity.ok(reservationService.getUserReservationInfoList(userReservationAllRequestDto));
        } catch (CheckInOutFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
