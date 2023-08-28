package com.app.diamondhotelbackend.service.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.function.Function;

public interface JwtService {

    String createToken(UserDetails userDetails, long expirationTime);

    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    Optional<UserDetails> validateToken(String token);

    long getAccessTokenExpiration();

    long getRefreshTokenExpiration();
}
