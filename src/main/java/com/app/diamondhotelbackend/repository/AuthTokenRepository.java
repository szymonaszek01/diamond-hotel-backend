package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.AuthToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {

    Optional<AuthToken> findByRefreshValue(String refreshToken);

    Optional<AuthToken> findByUserProfile(UserProfile userProfile);
}
