package com.app.diamondhotelbackend.service.jwt;

import com.app.diamondhotelbackend.util.ApplicationPropertiesUtil;
import com.app.diamondhotelbackend.util.ConstantUtil;
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
public class JwtServiceImpl implements JwtService {

    private final UserDetailsService userDetailsService;

    private final ApplicationPropertiesUtil applicationPropertiesUtil;

    @Override
    public String createToken(UserDetails userDetails, long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ConstantUtil.JWT_CLAIM_AUTHORITIES, List.of(userDetails.getAuthorities()));

        return Jwts.builder().setClaims(claims).setSubject(userDetails.getUsername()).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + expirationTime)).signWith(SignatureAlgorithm.HS256, applicationPropertiesUtil.getSecretKey()).compact();
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public Optional<UserDetails> validateToken(String token) {
        try {
            final String username = extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!username.equals(userDetails.getUsername()) || isTokenExpired(token)) {
                return Optional.empty();
            }
            return Optional.of(userDetails);

        } catch (UsernameNotFoundException e) {
            log.error(ConstantUtil.USER_PROFILE_NOT_FOUND_EXCEPTION);
            return Optional.empty();
        } catch (SignatureException e) {
            log.error(ConstantUtil.INVALID_TOKEN_SIGNATURE_EXCEPTION);
            return Optional.empty();
        } catch (MalformedJwtException e) {
            log.error(ConstantUtil.INVALID_TOKEN_EXCEPTION);
            return Optional.empty();
        } catch (ExpiredJwtException e) {
            log.error(ConstantUtil.TOKEN_IS_EXPIRED_EXCEPTION);
            return Optional.empty();
        } catch (UnsupportedJwtException e) {
            log.error(ConstantUtil.TOKEN_IS_UNSUPPORTED_EXCEPTION);
            return Optional.empty();
        } catch (IllegalArgumentException e) {
            log.error(ConstantUtil.TOKEN_CLAIMS_STRING_IS_EMPTY_EXCEPTION);
            return Optional.empty();
        }
    }

    @Override
    public long getAccessTokenExpiration() {
        return Long.parseLong(applicationPropertiesUtil.getAccessTokenExpiration());
    }

    @Override
    public long getRefreshTokenExpiration() {
        return Long.parseLong(applicationPropertiesUtil.getRefreshTokenExpiration());
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(applicationPropertiesUtil.getSecretKey()).parseClaimsJws(token).getBody();
    }
}
