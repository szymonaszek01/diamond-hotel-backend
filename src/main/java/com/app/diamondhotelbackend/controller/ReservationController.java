package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.common.PdfResponseDto;
import com.app.diamondhotelbackend.dto.reservation.request.ReservationCreateRequestDto;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.exception.ReservationProcessingException;
import com.app.diamondhotelbackend.exception.RoomProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.service.reservation.ReservationServiceImpl;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RequestMapping("/api/v1/reservation")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://diamond-hotel-frontend-react.vercel.app/"}, allowCredentials = "true")
public class ReservationController {

    private final ReservationServiceImpl reservationService;

    @PostMapping("/create")
    public ResponseEntity<Reservation> createReservation(@RequestBody ReservationCreateRequestDto reservationCreateRequestDto) {
        try {
            return ResponseEntity.ok(reservationService.createReservation(reservationCreateRequestDto));
        } catch (ReservationProcessingException | UserProfileProcessingException | RoomProcessingException |
                 IOException | StripeException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<Reservation> getReservationList(@RequestParam(value = "page") int page, @RequestParam(value = "size") int size, @RequestParam(value = "payment-status", defaultValue = "", required = false) String paymentStatus, @RequestParam(value = "sort", defaultValue = "[]", required = false) JSONArray jsonArray) {
        return reservationService.getReservationList(page, size, paymentStatus, jsonArray);
    }

    @GetMapping("/all/user-profile-id/{userProfileId}")
    public List<Reservation> getReservationListByUserProfileId(@PathVariable long userProfileId,
                                                               @RequestParam(value = "page") int page,
                                                               @RequestParam(value = "size") int size,
                                                               @RequestParam(value = "payment-status", defaultValue = "", required = false) String paymentStatus,
                                                               @RequestParam(value = "sort", defaultValue = "[]", required = false) JSONArray jsonArray) {
        return reservationService.getReservationListByUserProfileId(userProfileId, page, size, paymentStatus, jsonArray);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(reservationService.getReservationById(id));
        } catch (ReservationProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping(value = "/id/{id}/pdf")
    public ResponseEntity<PdfResponseDto> getReservationPdfDocumentById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(reservationService.getReservationPdfDocumentById(id));

        } catch (ReservationProcessingException | IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/all/number/user-profile-id/{userProfileId}")
    public ResponseEntity<Long> countReservationListByUserProfileId(@PathVariable long userProfileId) {
        try {
            return ResponseEntity.ok(reservationService.countReservationListByUserProfileId(userProfileId));
        } catch (UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/id/{id}/payment-token/{paymentToken}")
    public ResponseEntity<Reservation> updateReservationPayment(@PathVariable long id, @PathVariable String paymentToken) {
        try {
            return ResponseEntity.ok(reservationService.updateReservationPayment(id, paymentToken));
        } catch (ReservationProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Reservation> deleteReservationById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(reservationService.deleteReservationById(id));
        } catch (ReservationProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
