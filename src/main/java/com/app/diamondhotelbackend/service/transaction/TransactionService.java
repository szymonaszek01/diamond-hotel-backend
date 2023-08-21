package com.app.diamondhotelbackend.service.transaction;

import com.app.diamondhotelbackend.dto.shoppingcart.CostSummaryDto;
import com.app.diamondhotelbackend.dto.transaction.TransactionDto;
import com.app.diamondhotelbackend.dto.transaction.TransactionStatusInfoDto;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.Transaction;
import com.app.diamondhotelbackend.exception.InvalidTransactionStatusException;
import com.app.diamondhotelbackend.exception.TransactionNotFoundException;

import java.math.BigDecimal;

public interface TransactionService {

    Transaction findTransactionByCode(String code) throws TransactionNotFoundException;

    void updateTransactionAfterReservationCancellation(Reservation reservation);

    TransactionDto toTransactionDtoMapper(Transaction transaction);

    TransactionStatusInfoDto changeTransactionStatus(TransactionStatusInfoDto transactionStatusInfoDto) throws TransactionNotFoundException, InvalidTransactionStatusException;

    Transaction createNewTransaction(CostSummaryDto costSummaryDto);

    BigDecimal getTotalCost(Transaction transaction);
}
