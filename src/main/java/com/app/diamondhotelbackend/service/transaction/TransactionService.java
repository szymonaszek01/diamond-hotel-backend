package com.app.diamondhotelbackend.service.transaction;

import com.app.diamondhotelbackend.entity.Transaction;
import com.app.diamondhotelbackend.exception.TransactionProcessingException;

import java.math.BigDecimal;

public interface TransactionService {

    Transaction getTransactionById(long id) throws TransactionProcessingException;

    Transaction createTransaction(Transaction transaction);

    Transaction updateTransactionStatus(long id, String status) throws TransactionProcessingException;

    Transaction updateTransactionCost(long id, BigDecimal cost) throws TransactionProcessingException;

    Transaction updateTransactionToken(long id, String token) throws TransactionProcessingException;
}
