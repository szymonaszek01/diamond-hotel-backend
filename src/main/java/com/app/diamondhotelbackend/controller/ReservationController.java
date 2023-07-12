package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.reservation.*;
import com.app.diamondhotelbackend.exception.CheckInOutFormatException;
import com.app.diamondhotelbackend.exception.NotAllSelectedRoomsAvailableException;
import com.app.diamondhotelbackend.exception.ReservationNotFoundException;
import com.app.diamondhotelbackend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/api/v1/reservation")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://diamond-hotel-frontend.vercel.app", "http://localhost:4200", "http://localhost:3000"}, allowCredentials = "true")
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

    @PostMapping("/create/new")
    public ResponseEntity<UserReservationNewResponseDto> createNewReservation(@RequestBody UserReservationNewRequestDto userReservationNewRequestDto) {
        try {
            return ResponseEntity.ok(reservationService.createNewReservation(userReservationNewRequestDto));
        } catch (CheckInOutFormatException | NotAllSelectedRoomsAvailableException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/details/info")
    public ResponseEntity<UserReservationDetailsInfoResponseDto> getUserReservationDetailsInfo(@RequestBody UserReservationDetailsInfoRequestDto userReservationDetailsInfoRequestDto) {
        try {
            return ResponseEntity.ok(reservationService.getUserReservationDetailsInfo(userReservationDetailsInfoRequestDto));
        } catch (ReservationNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/id/{id}/cancel")
    public ResponseEntity<UserReservationCancellationResponseDto> deleteUserReservationDetails(@PathVariable long id) {
        try {
            return ResponseEntity.ok(reservationService.deleteReservationDetails(id));
        } catch (ReservationNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
