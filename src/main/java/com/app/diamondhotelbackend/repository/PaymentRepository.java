package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Page<Payment> findAllByStatus(String status, Pageable pageable);

    Page<Payment> findAllByReservationUserProfileId(long userProfileId, Pageable pageable);

    Page<Payment> findAllByStatusAndReservationUserProfileId(String status, long userProfileId, Pageable pageable);

    Long countAllByReservationUserProfile(UserProfile userProfile);
}
