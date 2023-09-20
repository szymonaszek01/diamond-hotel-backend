package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Payment;
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
public class PaymentRepositoryTests {

    @Autowired
    private PaymentRepository paymentRepository;

    private Payment payment;

    private List<Payment> paymentList;

    @BeforeEach
    public void init() {
        payment = Payment.builder()
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .token("token1")
                .build();

        paymentList = List.of(
                Payment.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .token("token1")
                        .build(),
                Payment.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .token("token2")
                        .build()
        );
    }

    @Test
    public void PaymentRepository_Save_ReturnsSavedPayment() {
        Payment savedPayment = paymentRepository.save(payment);

        Assertions.assertThat(savedPayment).isNotNull();
        Assertions.assertThat(savedPayment.getId()).isGreaterThan(0);
    }

    @Test
    public void PaymentRepository_FindAll_ReturnsPaymentList() {
        paymentRepository.saveAll(paymentList);
        List<Payment> foundPaymentList = paymentRepository.findAll();

        Assertions.assertThat(foundPaymentList).isNotNull();
        Assertions.assertThat(foundPaymentList.size()).isEqualTo(2);
    }

    @Test
    public void PaymentRepository_FindById_ReturnsOptionalPayment() {
        Payment savedPayment = paymentRepository.save(payment);
        Optional<Payment> paymentOptional = paymentRepository.findById((payment.getId()));

        Assertions.assertThat(paymentOptional).isPresent();
        Assertions.assertThat(paymentOptional.get().getId()).isEqualTo(savedPayment.getId());
    }

    @Test
    public void PaymentRepository_Update_ReturnsPayment() {
        Payment savedPayment = paymentRepository.save(payment);
        Optional<Payment> paymentOptional = paymentRepository.findById((savedPayment.getId()));

        Assertions.assertThat(paymentOptional).isPresent();
        Assertions.assertThat(paymentOptional.get().getId()).isEqualTo(savedPayment.getId());

        paymentOptional.get().setStatus(ConstantUtil.APPROVED);
        Payment updatedPayment = paymentRepository.save(paymentOptional.get());

        Assertions.assertThat(updatedPayment).isNotNull();
        Assertions.assertThat(updatedPayment.getStatus()).isEqualTo(ConstantUtil.APPROVED);
    }

    @Test
    public void PaymentRepository_Delete_ReturnsNothing() {
        Payment savedPayment = paymentRepository.save(payment);
        paymentRepository.deleteById(savedPayment.getId());
        Optional<Payment> paymentOptional = paymentRepository.findById(payment.getId());

        Assertions.assertThat(paymentOptional).isEmpty();
    }
}
