package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long>, JpaSpecificationExecutor<Reservation> {

    @Query("SELECT DISTINCT YEAR(r.checkIn) FROM Reservation r")
    List<Integer> findAllCheckInYears();

    @Query("SELECT DISTINCT YEAR(r.checkOut) FROM Reservation r")
    List<Integer> findAllCheckOutYears();
}
