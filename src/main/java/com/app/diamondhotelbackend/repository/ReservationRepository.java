package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findReservationByIdAndUserProfileId(long reservationId, long userProfileId);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE " +
            "r.room.id = :roomId AND " +
            "r.transaction.status <> 'CANCELLED' AND" +
            "((r.checkIn BETWEEN :checkIn AND :checkOut) OR " +
            "(r.checkOut BETWEEN :checkIn AND :checkOut) OR " +
            "((:checkIn BETWEEN r.checkIn AND r.checkOut) AND " +
            "(:checkOut BETWEEN r.checkIn AND r.checkOut)))")
    long countAllByRoomIdCheckInAndCheckOut(long roomId, LocalDateTime checkIn, LocalDateTime checkOut);

    List<Reservation> findAllByUserProfileId(long userProfileId);
}
