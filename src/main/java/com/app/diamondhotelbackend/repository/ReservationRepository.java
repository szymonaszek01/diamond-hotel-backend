package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.room.id=:roomId AND " +
            "((r.checkIn BETWEEN :start AND :end) OR " +
            "(r.checkOut BETWEEN :start AND :end) OR " +
            "((:start BETWEEN r.checkIn AND r.checkOut) AND " +
            "(:end BETWEEN r.checkIn AND r.checkOut)))"
    )
    long countAllByRoomIdAndOccupyStatus(long roomId, LocalDateTime start, LocalDateTime end);

}
