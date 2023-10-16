package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findAllByUserProfileIdOrderByIdDesc(long userProfileId, Pageable pageable);

    Page<Reservation> findAllByUserProfileIdAndPaymentStatusOrderByIdDesc(long userProfileId, String paymentStatus, Pageable pageable);

    Long countAllByUserProfile(UserProfile userProfile);
}
