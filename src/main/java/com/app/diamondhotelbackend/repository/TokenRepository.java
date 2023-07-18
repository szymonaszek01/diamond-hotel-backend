package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Token;
import com.app.diamondhotelbackend.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findTokenByRefreshValue(String refreshToken);

    Optional<Token> findTokenByUserProfile(UserProfile userProfile);
}
