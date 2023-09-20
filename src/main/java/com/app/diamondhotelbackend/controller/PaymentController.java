package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.payment.request.PaymentChargeRequestDto;
import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.exception.PaymentProcessingException;
import com.app.diamondhotelbackend.service.payment.PaymentServiceImpl;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/api/v1/payment")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
public class PaymentController {

    private final PaymentServiceImpl paymentService;

    @PutMapping("/charge")
    public ResponseEntity<Payment> chargePayment(@RequestBody PaymentChargeRequestDto paymentChargeRequestDto) {
        try {
            return ResponseEntity.ok(paymentService.chargePayment(paymentChargeRequestDto));
        } catch (PaymentProcessingException | StripeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/id/{id}/cancel")
    public ResponseEntity<Payment> cancelPayment(@PathVariable long id) {
        try {
            return ResponseEntity.ok(paymentService.cancelPayment(id));
        } catch (PaymentProcessingException | StripeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
