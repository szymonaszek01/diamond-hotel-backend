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
import org.springframework.test.context.ActiveProfiles;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
public class PaymentRepositoryTests {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Payment payment;

    private List<Payment> paymentList;

    private UserProfile savedUserProfile;

    private PageRequest pageRequest;

    @BeforeEach
    public void init() {
        payment = Payment.builder()
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .token("token1")
                .build();

        savedUserProfile = testEntityManager.persistAndFlush(
                UserProfile.builder()
                        .email("test.user@gmail.com")
                        .passportNumber("passportNumber1")
                        .build()
        );

        List<Reservation> savedReservationList = List.of(
                testEntityManager.persistAndFlush(
                        Reservation.builder()
                                .checkIn(Date.valueOf("2023-10-24"))
                                .checkOut(Date.valueOf("2023-10-27"))
                                .adults(2)
                                .children(2)
                                .userProfile(savedUserProfile)
                                .build()
                ),
                testEntityManager.persistAndFlush(Reservation.builder()
                        .checkIn(Date.valueOf("2023-11-24"))
                        .checkOut(Date.valueOf("2023-11-27"))
                        .adults(2)
                        .children(2)
                        .userProfile(savedUserProfile)
                        .build()
                ),
                testEntityManager.persistAndFlush(
                        Reservation.builder()
                                .checkIn(Date.valueOf("2023-12-24"))
                                .checkOut(Date.valueOf("2023-12-27"))
                                .adults(2)
                                .children(2)
                                .userProfile(savedUserProfile)
                                .build()
                ),
                testEntityManager.persistAndFlush(
                        Reservation.builder()
                                .checkIn(Date.valueOf("2024-01-24"))
                                .checkOut(Date.valueOf("2024-01-27"))
                                .adults(2)
                                .children(2)
                                .userProfile(savedUserProfile)
                                .build()
                )
        );

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
        paymentRepository.saveAll(paymentList);
        Page<Payment> paymentPage = paymentRepository.findAll(pageRequest);

        Assertions.assertThat(paymentPage).isNotNull();
    }

    @Test
    public void PaymentRepository_FindAllByStatus_ReturnsPaymentPage() {
        paymentList.forEach(p -> p.setStatus(ConstantUtil.APPROVED));

        paymentRepository.saveAll(paymentList);
        Page<Payment> paymentPage = paymentRepository.findAllByStatus(ConstantUtil.APPROVED, pageRequest);

        Assertions.assertThat(paymentPage).isNotNull();
    }

    @Test
    public void PaymentRepository_FindAllByReservationUserProfileId_ReturnsPaymentPage() {
        paymentRepository.saveAll(paymentList);
        Page<Payment> paymentPage = paymentRepository.findAllByReservationUserProfileId(savedUserProfile.getId(), pageRequest);

        Assertions.assertThat(paymentPage).isNotNull();
    }

    @Test
    public void PaymentRepository_FindAllByStatusAndReservationUserProfileId_ReturnsPaymentPage() {
        paymentRepository.saveAll(paymentList);
        Page<Payment> paymentPage = paymentRepository.findAllByStatusAndReservationUserProfileId(payment.getStatus(), savedUserProfile.getId(), pageRequest);

        Assertions.assertThat(paymentPage).isNotNull();
    }

    @Test
    public void PaymentRepository_CountAllByReservationUserProfile_ReturnsLong() {
        paymentRepository.saveAll(paymentList);

        Long countedPaymentList = paymentRepository.countAllByReservationUserProfile(savedUserProfile);

        Assertions.assertThat(countedPaymentList).isNotNull();
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
