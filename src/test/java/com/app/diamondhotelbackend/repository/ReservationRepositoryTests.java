package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Flight;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.entity.UserProfile;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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

    @BeforeEach
    public void init() {
        UserProfile savedUserProfile = testEntityManager.persistAndFlush(
                UserProfile.builder()
                        .email("email1")
                        .passportNumber("passportNumber1")
                        .build()
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
                                .build()
                ),
                testEntityManager.persistAndFlush(
                        Payment.builder()
                                .createdAt(new Date(System.currentTimeMillis()))
                                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                                .token("token2")
                                .build()
                )
        );

        reservation = Reservation.builder()
                .checkIn(Date.valueOf("2023-11-24"))
                .checkOut(Date.valueOf("2023-11-27"))
                .adults(2)
                .children(2)
                .userProfile(savedUserProfile)
                .flight(savedFlightList.get(0))
                .payment(savedPaymentList.get(0))
                .build();

        reservationList = List.of(
                Reservation.builder()
                        .checkIn(Date.valueOf("2023-11-24"))
                        .checkOut(Date.valueOf("2023-11-27"))
                        .adults(2)
                        .children(2)
                        .userProfile(savedUserProfile)
                        .flight(savedFlightList.get(0))
                        .payment(savedPaymentList.get(0))
                        .build(),
                Reservation.builder()
                        .checkIn(Date.valueOf("2023-12-24"))
                        .checkOut(Date.valueOf("2023-12-27"))
                        .adults(2)
                        .children(2)
                        .userProfile(savedUserProfile)
                        .flight(savedFlightList.get(1))
                        .payment(savedPaymentList.get(1))
                        .build()
        );
    }

    @Test
    public void ReservationRepository_Save_ReturnsSavedReservation() {
        Reservation savedReservation = reservationRepository.save(reservation);

        Assertions.assertThat(savedReservation).isNotNull();
        Assertions.assertThat(savedReservation.getId()).isGreaterThan(0);
    }

    @Test
    public void ReservationRepository_FindAll_ReturnsReservationList() {
        reservationRepository.saveAll(reservationList);
        List<Reservation> foundReservationList = reservationRepository.findAll();

        Assertions.assertThat(foundReservationList).isNotNull();
        Assertions.assertThat(foundReservationList.size()).isEqualTo(2);
    }

    @Test
    public void ReservationRepository_FindById_ReturnsOptionalReservation() {
        Reservation savedReservation = reservationRepository.save(reservation);
        Optional<Reservation> reservationOptional = reservationRepository.findById((reservation.getId()));

        Assertions.assertThat(reservationOptional).isPresent();
        Assertions.assertThat(reservationOptional.get().getId()).isEqualTo(savedReservation.getId());
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
