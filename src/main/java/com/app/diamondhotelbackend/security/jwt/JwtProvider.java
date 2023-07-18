package com.app.diamondhotelbackend.security.jwt;

import com.app.diamondhotelbackend.util.Constant;
import com.app.diamondhotelbackend.util.JwtPropertiesProvider;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class JwtProvider {

    private final UserDetailsService userDetailsService;

    private final JwtPropertiesProvider jwtPropertiesProvider;

    public String createToken(UserDetails userDetails, long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constant.JWT_CLAIM_AUTHORITIES, List.of(userDetails.getAuthorities()));

        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername()).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + expirationTime)).signWith(SignatureAlgorithm.HS256, jwtPropertiesProvider.getSecretKey()).compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Optional<UserDetails> validateToken(String token) {
        try {
            final String username = extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!username.equals(userDetails.getUsername()) || isTokenExpired(token)) {
                return Optional.empty();
            }
            return Optional.of(userDetails);

        } catch (UsernameNotFoundException e) {
            log.error(Constant.USER_PROFILE_NOT_FOUND_EXCEPTION);
            return Optional.empty();
        } catch (SignatureException e) {
            log.error(Constant.INVALID_TOKEN_SIGNATURE_EXCEPTION);
            return Optional.empty();
        } catch (MalformedJwtException e) {
            log.error(Constant.INVALID_TOKEN_EXCEPTION);
            return Optional.empty();
        } catch (ExpiredJwtException e) {
            log.error(Constant.TOKEN_IS_EXPIRED_EXCEPTION);
            return Optional.empty();
        } catch (UnsupportedJwtException e) {
            log.error(Constant.TOKEN_IS_UNSUPPORTED_EXCEPTION);
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            log.error(Constant.TOKEN_CLAIMS_STRING_IS_EMPTY_EXCEPTION);
            return Optional.empty();
        }
    }

    public long getAccessTokenExpiration() {
        return Long.parseLong(jwtPropertiesProvider.getAccessTokenExpiration());
    }

    public long getRefreshTokenExpiration() {
        return Long.parseLong(jwtPropertiesProvider.getRefreshTokenExpiration());
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtPropertiesProvider.getSecretKey()).parseClaimsJws(token).getBody();
    }
}
