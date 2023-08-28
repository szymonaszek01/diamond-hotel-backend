package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.security.jwt.CustomUserDetails;
import com.app.diamondhotelbackend.service.jwt.JwtServiceImpl;
import com.app.diamondhotelbackend.util.Constant;
import com.app.diamondhotelbackend.util.JwtPropertiesProvider;
import io.jsonwebtoken.Claims;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTests {

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtPropertiesProvider jwtPropertiesProvider;

    private UserDetails userDetails;

    private static final String SECRET_KEY = "SecretKey";

    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15;

    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60;

    @BeforeEach
    public void init() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        UserProfile userProfile = UserProfile.builder()
                .id(1)
                .email("ala-gembala@wp.pl")
                .password(passwordEncoder.encode("#Test1111"))
                .passportNumber("ZF005401499")
                .role(Constant.USER)
                .authProvider(Constant.LOCAL)
                .accountConfirmed(false)
                .build();

        userDetails = new CustomUserDetails(
                userProfile.getId(),
                userProfile.getEmail(),
                userProfile.getPassword(),
                List.of(new SimpleGrantedAuthority(userProfile.getRole()))
        );
    }

    @Test
    public void JwtService_CreateToken_ReturnsString() {
        when(jwtPropertiesProvider.getSecretKey()).thenReturn(SECRET_KEY);

        String createdToken = jwtService.createToken(userDetails, ACCESS_TOKEN_EXPIRATION);

        Assertions.assertThat(createdToken).isNotNull();
    }

    @Test
    public void JwtService_ExtractUsername_ReturnsString() {
        when(jwtPropertiesProvider.getSecretKey()).thenReturn(SECRET_KEY);

        String createdToken = jwtService.createToken(userDetails, ACCESS_TOKEN_EXPIRATION);
        String extractedUsername = jwtService.extractUsername(createdToken);

        Assertions.assertThat(extractedUsername).isNotNull();
        Assertions.assertThat(extractedUsername).isEqualTo(userDetails.getUsername());
    }

    @Test
    public void JwtService_ExtractClaim_ReturnsT() {
        when(jwtPropertiesProvider.getSecretKey()).thenReturn(SECRET_KEY);

        String createdToken = jwtService.createToken(userDetails, ACCESS_TOKEN_EXPIRATION);
        String extractedId = jwtService.extractClaim(createdToken, Claims::getId);

        Assertions.assertThat(extractedId).isNull();
    }

    @Test
    public void JwtService_ValidateToken_ReturnsOptionalUserDetails() {
        when(jwtPropertiesProvider.getSecretKey()).thenReturn(SECRET_KEY);
        when(userDetailsService.loadUserByUsername(Mockito.any(String.class))).thenReturn(userDetails);

        String createdToken = jwtService.createToken(userDetails, ACCESS_TOKEN_EXPIRATION);
        Optional<UserDetails> optionalUserDetails = jwtService.validateToken(createdToken);

        Assertions.assertThat(optionalUserDetails).isPresent();
        Assertions.assertThat(optionalUserDetails.get().getUsername()).isEqualTo(userDetails.getUsername());
        Assertions.assertThat(optionalUserDetails.get().getPassword()).isEqualTo(userDetails.getPassword());
        Assertions.assertThat(optionalUserDetails.get().getAuthorities()).isEqualTo(userDetails.getAuthorities());
    }

    @Test
    public void JwtService_GetAccessTokenExpiration_ReturnsLong() {
        when(jwtPropertiesProvider.getAccessTokenExpiration()).thenReturn(String.valueOf(ACCESS_TOKEN_EXPIRATION));

        long expiration = jwtService.getAccessTokenExpiration();

        Assertions.assertThat(expiration).isNotNull();
        Assertions.assertThat(expiration).isEqualTo(ACCESS_TOKEN_EXPIRATION);
    }

    @Test
    public void JwtService_getRefreshTokenExpiration_ReturnsLong() {
        when(jwtPropertiesProvider.getRefreshTokenExpiration()).thenReturn(String.valueOf(REFRESH_TOKEN_EXPIRATION));

        long expiration = jwtService.getRefreshTokenExpiration();

        Assertions.assertThat(expiration).isNotNull();
        Assertions.assertThat(expiration).isEqualTo(REFRESH_TOKEN_EXPIRATION);
    }
}
