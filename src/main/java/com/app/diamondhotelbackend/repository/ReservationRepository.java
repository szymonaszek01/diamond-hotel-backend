package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
