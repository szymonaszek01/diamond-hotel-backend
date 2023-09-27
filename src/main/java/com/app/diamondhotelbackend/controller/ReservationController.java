package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.reservation.request.ReservationCreateRequestDto;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.exception.ReservationProcessingException;
import com.app.diamondhotelbackend.exception.RoomProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.service.reservation.ReservationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
                 IOException e) {
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

    @GetMapping("/all")
    public List<Reservation> getReservationList(@RequestParam(value = "page") int page, @RequestParam(value = "size") int size) {
        return reservationService.getReservationList(page, size);
    }

    @GetMapping("/all/user-profile-id/{userProfileId}")
    public List<Reservation> getReservationListByUserProfileId(@PathVariable long userProfileId, @RequestParam(value = "page") int page, @RequestParam(value = "size") int size) {
        return reservationService.getReservationListByUserProfileId(userProfileId, page, size);
    }

    @GetMapping(value = "/id/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getReservationPdfDocument(@PathVariable long id) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; " + "filename=reservation" + id + ".pdf");

            return ResponseEntity.ok().headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(reservationService.getReservationPdfDocument(id));

        } catch (ReservationProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
