package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Flight;
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
public class ReservationRepositoryTests {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Reservation reservation;

    private List<Reservation> reservationList;

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

        List<Flight> savedFlightList = List.of(
                testEntityManager.persistAndFlush(
                        Flight.builder()
                                .flightNumber("flightNumber1")
                                .build()
                ),
                testEntityManager.persistAndFlush(
                        Flight.builder()
                                .flightNumber("flightNumber2")
                                .build()
                )
        );

        List<Payment> savedPaymentList = List.of(
                testEntityManager.persistAndFlush(
                        Payment.builder()
                                .createdAt(new Date(System.currentTimeMillis()))
                                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                                .token("token1")
                                .status(ConstantUtil.APPROVED)
                                .build()
                ),
                testEntityManager.persistAndFlush(
                        Payment.builder()
                                .createdAt(new Date(System.currentTimeMillis()))
                                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                                .token("token2")
                                .status(ConstantUtil.WAITING_FOR_PAYMENT)
                                .build()
                ),
                testEntityManager.persistAndFlush(
                        Payment.builder()
                                .createdAt(new Date(System.currentTimeMillis()))
                                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                                .token("token3")
                                .status(ConstantUtil.WAITING_FOR_PAYMENT)
                                .build()
                ),
                testEntityManager.persistAndFlush(
                        Payment.builder()
                                .createdAt(new Date(System.currentTimeMillis()))
                                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                                .token("token4")
                                .status(ConstantUtil.WAITING_FOR_PAYMENT)
                                .build()
                ),
                testEntityManager.persistAndFlush(
                        Payment.builder()
                                .createdAt(new Date(System.currentTimeMillis()))
                                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                                .token("token5")
                                .status(ConstantUtil.WAITING_FOR_PAYMENT)
                                .build()
                ),
                testEntityManager.persistAndFlush(
                        Payment.builder()
                                .createdAt(new Date(System.currentTimeMillis()))
                                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                                .token("token6")
                                .status(ConstantUtil.WAITING_FOR_PAYMENT)
                                .build()
                )
        );

        reservation = Reservation.builder()
                .checkIn(Date.valueOf("2023-10-24"))
                .checkOut(Date.valueOf("2023-10-27"))
                .adults(2)
                .children(2)
                .userProfile(savedUserProfileList.get(0))
                .flight(savedFlightList.get(0))
                .payment(savedPaymentList.get(1))
                .build();

        reservationList = List.of(
                Reservation.builder()
                        .checkIn(Date.valueOf("2023-10-24"))
                        .checkOut(Date.valueOf("2023-10-27"))
                        .adults(2)
                        .children(2)
                        .userProfile(savedUserProfileList.get(0))
                        .flight(savedFlightList.get(0))
                        .payment(savedPaymentList.get(1))
                        .build(),
                Reservation.builder()
                        .checkIn(Date.valueOf("2023-11-24"))
                        .checkOut(Date.valueOf("2023-11-27"))
                        .adults(2)
                        .children(2)
                        .userProfile(savedUserProfileList.get(0))
                        .flight(savedFlightList.get(0))
                        .payment(savedPaymentList.get(2))
                        .build(),
                Reservation.builder()
                        .checkIn(Date.valueOf("2023-12-24"))
                        .checkOut(Date.valueOf("2023-12-27"))
                        .adults(2)
                        .children(2)
                        .userProfile(savedUserProfileList.get(0))
                        .flight(savedFlightList.get(1))
                        .payment(savedPaymentList.get(3))
                        .build(),
                Reservation.builder()
                        .checkIn(Date.valueOf("2024-01-24"))
                        .checkOut(Date.valueOf("2024-01-27"))
                        .adults(2)
                        .children(2)
                        .userProfile(savedUserProfileList.get(1))
                        .flight(savedFlightList.get(0))
                        .payment(savedPaymentList.get(4))
                        .build(),
                Reservation.builder()
                        .checkIn(Date.valueOf("2024-02-24"))
                        .checkOut(Date.valueOf("2024-02-27"))
                        .adults(2)
                        .children(2)
                        .userProfile(savedUserProfileList.get(1))
                        .flight(savedFlightList.get(0))
                        .payment(savedPaymentList.get(5))
                        .build(),
                Reservation.builder()
                        .checkIn(Date.valueOf("2024-03-24"))
                        .checkOut(Date.valueOf("2024-03-27"))
                        .adults(2)
                        .children(2)
                        .userProfile(savedUserProfileList.get(1))
                        .flight(savedFlightList.get(1))
                        .payment(savedPaymentList.get(6))
                        .build()
        );

        pageRequest = PageRequest.of(0, 3);
    }

    @Test
    public void ReservationRepository_Save_ReturnsSavedReservation() {
        Reservation savedReservation = reservationRepository.save(reservation);

        Assertions.assertThat(savedReservation).isNotNull();
        Assertions.assertThat(savedReservation.getId()).isGreaterThan(0);
    }

    @Test
    public void ReservationRepository_FindAll_ReturnsReservationPage() {
        reservationRepository.saveAll(reservationList);
        Page<Reservation> reservationPage = reservationRepository.findAll(pageRequest);

        Assertions.assertThat(reservationPage).isNotNull();
    }

    @Test
    public void ReservationRepository_FindAllByPaymentStatus_ReturnsReservationPage() {
        reservationRepository.saveAll(reservationList);
        Page<Reservation> reservationPage = reservationRepository.findAllByPaymentStatus(ConstantUtil.APPROVED, pageRequest);

        Assertions.assertThat(reservationPage).isNotNull();
    }

    @Test
    public void ReservationRepository_FindAllByUserProfileId_ReturnsReservationPage() {
        pageRequest = pageRequest.withPage(1);

        reservationRepository.saveAll(reservationList);
        Page<Reservation> reservationPage = reservationRepository.findAllByUserProfileId(1L, pageRequest);

        Assertions.assertThat(reservationPage).isNotNull();
    }

    @Test
    public void ReservationRepository_FindAllByUserProfileIdAndPaymentStatus_ReturnsReservationPage() {
        pageRequest = pageRequest.withPage(1);

        reservationRepository.saveAll(reservationList);
        Page<Reservation> reservationPage = reservationRepository.findAllByUserProfileIdAndPaymentStatus(1L, ConstantUtil.APPROVED, pageRequest);

        Assertions.assertThat(reservationPage).isNotNull();
    }

    @Test
    public void ReservationRepository_FindById_ReturnsOptionalReservation() {
        Reservation savedReservation = reservationRepository.save(reservation);
        Optional<Reservation> reservationOptional = reservationRepository.findById((reservation.getId()));

        Assertions.assertThat(reservationOptional).isPresent();
        Assertions.assertThat(reservationOptional.get().getId()).isEqualTo(savedReservation.getId());
    }

    @Test
    public void ReservationRepository_Count_ReturnsLong() {
        reservationRepository.saveAll(reservationList);

        Long countedReservationList = reservationRepository.count();

        Assertions.assertThat(countedReservationList).isNotNull();
        Assertions.assertThat(countedReservationList).isEqualTo(6);
    }

    @Test
    public void ReservationRepository_CountAllByUserProfile_ReturnsLong() {
        reservationRepository.saveAll(reservationList);

        Long countedReservationList = reservationRepository.countAllByUserProfile(reservation.getUserProfile());

        Assertions.assertThat(countedReservationList).isNotNull();
        Assertions.assertThat(countedReservationList).isEqualTo(3);
    }

    @Test
    public void ReservationRepository_Update_ReturnsReservation() {
        Reservation savedReservation = reservationRepository.save(reservation);
        Optional<Reservation> reservationOptional = reservationRepository.findById((savedReservation.getId()));

        Assertions.assertThat(reservationOptional).isPresent();
        Assertions.assertThat(reservationOptional.get().getId()).isEqualTo(savedReservation.getId());

        reservationOptional.get().setAdults(1);
        Reservation updatedReservation = reservationRepository.save(reservationOptional.get());

        Assertions.assertThat(updatedReservation).isNotNull();
        Assertions.assertThat(updatedReservation.getAdults()).isEqualTo(1);
    }

    @Test
    public void ReservationRepository_Delete_ReturnsNothing() {
        Reservation savedReservation = reservationRepository.save(reservation);
        reservationRepository.deleteById(savedReservation.getId());
        Optional<Reservation> reservationOptional = reservationRepository.findById(reservation.getId());

        Assertions.assertThat(reservationOptional).isEmpty();
    }
}
