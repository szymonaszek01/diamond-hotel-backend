package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Transaction;
import com.app.diamondhotelbackend.util.ConstantUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
public class TransactionRepositoryTests {

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction transaction;

    private List<Transaction> transactionList;

    @BeforeEach
    public void init() {
        transaction = Transaction.builder()
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .token("token1")
                .build();

        transactionList = List.of(
                Transaction.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .token("token1")
                        .build(),
                Transaction.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .token("token2")
                        .build()
        );
    }

    @Test
    public void TransactionRepository_Save_ReturnsSavedTransaction() {
        Transaction savedTransaction = transactionRepository.save(transaction);

        Assertions.assertThat(savedTransaction).isNotNull();
        Assertions.assertThat(savedTransaction.getId()).isGreaterThan(0);
    }

    @Test
    public void TransactionRepository_FindAll_ReturnsTransactionList() {
        transactionRepository.saveAll(transactionList);
        List<Transaction> foundTransactionList = transactionRepository.findAll();

        Assertions.assertThat(foundTransactionList).isNotNull();
        Assertions.assertThat(foundTransactionList.size()).isEqualTo(2);
    }

    @Test
    public void TransactionRepository_FindById_ReturnsOptionalTransaction() {
        Transaction savedTransaction = transactionRepository.save(transaction);
        Optional<Transaction> transactionOptional = transactionRepository.findById((transaction.getId()));

        Assertions.assertThat(transactionOptional).isPresent();
        Assertions.assertThat(transactionOptional.get().getId()).isEqualTo(savedTransaction.getId());
    }

    @Test
    public void TransactionRepository_Update_ReturnsTransaction() {
        Transaction savedTransaction = transactionRepository.save(transaction);
        Optional<Transaction> transactionOptional = transactionRepository.findById((savedTransaction.getId()));

        Assertions.assertThat(transactionOptional).isPresent();
        Assertions.assertThat(transactionOptional.get().getId()).isEqualTo(savedTransaction.getId());

        transactionOptional.get().setStatus(ConstantUtil.APPROVED);
        Transaction updatedTransaction = transactionRepository.save(transactionOptional.get());

        Assertions.assertThat(updatedTransaction).isNotNull();
        Assertions.assertThat(updatedTransaction.getStatus()).isEqualTo(ConstantUtil.APPROVED);
    }

    @Test
    public void TransactionRepository_Delete_ReturnsNothing() {
        Transaction savedTransaction = transactionRepository.save(transaction);
        transactionRepository.deleteById(savedTransaction.getId());
        Optional<Transaction> TransactionOptional = transactionRepository.findById(transaction.getId());

        Assertions.assertThat(TransactionOptional).isEmpty();
    }
}
