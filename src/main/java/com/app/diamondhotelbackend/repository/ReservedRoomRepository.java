package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.ReservedRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservedRoomRepository extends JpaRepository<ReservedRoom, Long> {

    @Query("SELECT rr FROM ReservedRoom rr WHERE " +
            "rr.reservation.transaction.status <> 'CANCELLED' AND" +
            "((rr.reservation.checkIn BETWEEN :checkIn AND :checkOut) OR " +
            "(rr.reservation.checkOut BETWEEN :checkIn AND :checkOut) OR " +
            "((:checkIn BETWEEN rr.reservation.checkIn AND rr.reservation.checkOut) AND " +
            "(:checkOut BETWEEN rr.reservation.checkIn AND rr.reservation.checkOut)))")
    List<ReservedRoom> findAllByReservationCheckInAndReservationCheckOut(Date checkIn, Date checkOut);

    List<ReservedRoom> findAllByReservationId(long reservationId);
}
