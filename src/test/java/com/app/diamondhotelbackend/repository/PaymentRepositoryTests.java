package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.util.ConstantUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static com.app.diamondhotelbackend.specification.PaymentSpecification.paymentStatusEqual;

@DataJpaTest
@ActiveProfiles("test")
public class PaymentRepositoryTests {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Payment payment;

    private List<Payment> paymentList;

    private PageRequest pageRequest;

    @BeforeEach
    public void init() {
        List<UserProfile> savedUserProfileList = List.of(
                testEntityManager.persistAndFlush(
                        UserProfile.builder()
                                .email("email1")
                                .passportNumber("passportNumber1")
                                .build()
                ),
                testEntityManager.persistAndFlush(
                        UserProfile.builder()
                                .email("email2")
                                .passportNumber("passportNumber2")
                                .build()
                )
        );

        List<Reservation> savedReservationList = List.of(
                testEntityManager.persistAndFlush(
                        Reservation.builder()
                                .checkIn(Date.valueOf("2023-10-24"))
                                .checkOut(Date.valueOf("2023-10-27"))
                                .adults(2)
                                .children(2)
                                .userProfile(savedUserProfileList.get(0))
                                .build()
                ),
                testEntityManager.persistAndFlush(Reservation.builder()
                        .checkIn(Date.valueOf("2023-11-24"))
                        .checkOut(Date.valueOf("2023-11-27"))
                        .adults(2)
                        .children(2)
                        .userProfile(savedUserProfileList.get(0))
                        .build()
                ),
                testEntityManager.persistAndFlush(
                        Reservation.builder()
                                .checkIn(Date.valueOf("2023-12-24"))
                                .checkOut(Date.valueOf("2023-12-27"))
                                .adults(2)
                                .children(2)
                                .userProfile(savedUserProfileList.get(1))
                                .build()
                ),
                testEntityManager.persistAndFlush(
                        Reservation.builder()
                                .checkIn(Date.valueOf("2024-01-24"))
                                .checkOut(Date.valueOf("2024-01-27"))
                                .adults(2)
                                .children(2)
                                .userProfile(savedUserProfileList.get(1))
                                .build()
                )
        );

        payment = Payment.builder()
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .reservation(savedReservationList.get(0))
                .token("token1")
                .build();

        paymentList = List.of(
                Payment.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .reservation(savedReservationList.get(0))
                        .token("token1")
                        .build(),
                Payment.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .reservation(savedReservationList.get(1))
                        .token("token2")
                        .build(),
                Payment.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .reservation(savedReservationList.get(2))
                        .token("token3")
                        .build(),
                Payment.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .reservation(savedReservationList.get(3))
                        .token("token4")
                        .build()
        );

        pageRequest = PageRequest.of(0, 3);
    }

    @Test
    public void PaymentRepository_Save_ReturnsSavedPayment() {
        Payment savedPayment = paymentRepository.save(payment);

        Assertions.assertThat(savedPayment).isNotNull();
        Assertions.assertThat(savedPayment.getId()).isGreaterThan(0);
    }

    @Test
    public void PaymentRepository_FindAll_ReturnsPaymentPage() {
        Specification<Payment> paymentSpecification = Specification.where(paymentStatusEqual(ConstantUtil.WAITING_FOR_PAYMENT));
        paymentRepository.saveAll(paymentList);
        Page<Payment> paymentPage = paymentRepository.findAll(paymentSpecification, pageRequest);

        Assertions.assertThat(paymentPage).isNotNull();
    }

    @Test
    public void PaymentRepository_FindById_ReturnsOptionalPayment() {
        Payment savedPayment = paymentRepository.save(payment);
        Optional<Payment> paymentOptional = paymentRepository.findById((payment.getId()));

        Assertions.assertThat(paymentOptional).isPresent();
        Assertions.assertThat(paymentOptional.get().getId()).isEqualTo(savedPayment.getId());
    }

    @Test
    public void PaymentRepository_Count_ReturnsLong() {
        paymentRepository.saveAll(paymentList);

        Long countedPaymentList = paymentRepository.count();

        Assertions.assertThat(countedPaymentList).isNotNull();
        Assertions.assertThat(countedPaymentList).isEqualTo(4);
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
