package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.common.PdfResponseDto;
import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.exception.ReservationProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.service.payment.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RequestMapping("/api/v1/payment")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://diamond-hotel-frontend-react.vercel.app/"}, allowCredentials = "true")
public class PaymentController {

    private final PaymentServiceImpl paymentService;

    @GetMapping("/all")
    public List<Payment> getPaymentList(@RequestParam(value = "page") int page, @RequestParam(value = "size") int size) {
        return paymentService.getPaymentList(page, size);
    }

    @GetMapping("/all/user-profile-id/{userProfileId}")
    public List<Payment> getPaymentListByUserProfileId(@PathVariable long userProfileId,
                                                       @RequestParam(value = "page") int page,
                                                       @RequestParam(value = "size") int size,
                                                       @RequestParam(value = "payment-status", defaultValue = "", required = false) String status) {
        return paymentService.getPaymentListByUserProfileId(userProfileId, page, size, status);
    }

    @GetMapping(value = "/id/{id}/pdf")
    public ResponseEntity<PdfResponseDto> getPaymentPdfDocumentById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(paymentService.getPaymentPdfDocumentById(id));

        } catch (ReservationProcessingException | IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/all/number/user-profile-id/{userProfileId}")
    public ResponseEntity<Long> countPaymentListByUserProfileId(@PathVariable long userProfileId) {
        try {
            return ResponseEntity.ok(paymentService.countPaymentListByUserProfileId(userProfileId));
        } catch (UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
