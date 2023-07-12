package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.transaction.TransactionStatusInfoDto;
import com.app.diamondhotelbackend.exception.InvalidTransactionStatusException;
import com.app.diamondhotelbackend.exception.TransactionNotFoundException;
import com.app.diamondhotelbackend.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/api/v1/transaction")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://diamond-hotel-frontend.vercel.app", "http://localhost:4200", "http://localhost:3000"}, allowCredentials = "true")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/change/status")
    public ResponseEntity<TransactionStatusInfoDto> changeReservationStatus(@RequestBody TransactionStatusInfoDto transactionStatusInfoDto) {
        try {
            return ResponseEntity.ok(transactionService.changeTransactionStatus(transactionStatusInfoDto));
        } catch (TransactionNotFoundException | InvalidTransactionStatusException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
