package com.app.diamondhotelbackend.service.transaction;


import com.app.diamondhotelbackend.entity.Transaction;
import com.app.diamondhotelbackend.exception.TransactionProcessingException;
import com.app.diamondhotelbackend.repository.TransactionRepository;
import com.app.diamondhotelbackend.util.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private TransactionRepository transactionRepository;

    @Override
    public Transaction getTransactionById(long id) throws TransactionProcessingException {
        return transactionRepository.findById(id).orElseThrow(() -> new TransactionProcessingException(ConstantUtil.TRANSACTION_NOT_FOUND_EXCEPTION));
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction updateTransactionStatus(long id, String status) throws TransactionProcessingException {
        Transaction transaction = getTransactionById(id);
        transaction.setStatus(status);

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction updateTransactionCost(long id, BigDecimal cost) throws TransactionProcessingException {
        Transaction transaction = getTransactionById(id);
        transaction.setCost(cost);

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction updateTransactionToken(long id, String token) throws TransactionProcessingException {
        Transaction transaction = getTransactionById(id);
        transaction.setToken(token);

        return transactionRepository.save(transaction);
    }
}
