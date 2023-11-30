package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByEmail(String email);

    List<UserProfile> findAllByCreatedAtBetween(Date min, Date max);

    @Query("SELECT DISTINCT YEAR(up.createdAt) FROM UserProfile up")
    List<Integer> findAllCreatedAtYears();
}
