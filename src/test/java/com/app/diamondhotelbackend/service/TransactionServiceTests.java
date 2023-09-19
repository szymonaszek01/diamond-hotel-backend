package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.entity.Transaction;
import com.app.diamondhotelbackend.repository.TransactionRepository;
import com.app.diamondhotelbackend.service.transaction.TransactionServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    private Transaction transaction;

    private Transaction updatedTransaction;

    @BeforeEach
    public void init() {
        transaction = Transaction.builder()
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .status(ConstantUtil.WAITING_FOR_PAYMENT)
                .build();

        updatedTransaction = Transaction.builder()
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .token("token1")
                .cost(BigDecimal.valueOf(1650))
                .status(ConstantUtil.APPROVED)
                .build();
    }

    @Test
    public void TransactionService_CreateTransaction_ReturnsTransaction() {
        when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(transaction);

        Transaction savedTransaction = transactionService.createTransaction(transaction);

        Assertions.assertThat(savedTransaction).isNotNull();
        Assertions.assertThat(savedTransaction.getToken()).isEqualTo(transaction.getToken());
    }

    @Test
    public void TransactionService_UpdateTransactionStatus_ReturnsTransaction() {
        when(transactionRepository.findById(Mockito.any(long.class))).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(updatedTransaction);

        Transaction savedTransaction = transactionService.updateTransactionStatus(1, ConstantUtil.APPROVED);

        Assertions.assertThat(savedTransaction).isNotNull();
        Assertions.assertThat(savedTransaction.getStatus()).isEqualTo(updatedTransaction.getStatus());
    }

    @Test
    public void TransactionService_UpdateTransactionCost_ReturnsTransaction() {
        when(transactionRepository.findById(Mockito.any(long.class))).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(updatedTransaction);

        Transaction savedTransaction = transactionService.updateTransactionCost(1, BigDecimal.valueOf(1650));

        Assertions.assertThat(savedTransaction).isNotNull();
        Assertions.assertThat(savedTransaction.getCost()).isEqualTo(updatedTransaction.getCost());
    }

    @Test
    public void TransactionService_UpdateTransactionToken_ReturnsTransaction() {
        when(transactionRepository.findById(Mockito.any(long.class))).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(updatedTransaction);

        Transaction savedTransaction = transactionService.updateTransactionToken(1, "token1");

        Assertions.assertThat(savedTransaction).isNotNull();
        Assertions.assertThat(savedTransaction.getToken()).isEqualTo(updatedTransaction.getToken());
    }
}
