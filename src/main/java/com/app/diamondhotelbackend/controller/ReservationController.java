package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.reservation.request.ReservationCreateRequestDto;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.exception.ReservationProcessingException;
import com.app.diamondhotelbackend.exception.RoomProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.service.reservation.ReservationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/api/v1/reservation")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
public class ReservationController {

    private final ReservationServiceImpl reservationService;

    @PostMapping("/create")
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationCreateRequestDto reservationCreateRequestDto) {
        try {
            return ResponseEntity.ok(reservationService.createReservation(reservationCreateRequestDto));
        } catch (ReservationProcessingException | UserProfileProcessingException | RoomProcessingException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(reservationService.getReservationById(id));
        } catch (ReservationProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
