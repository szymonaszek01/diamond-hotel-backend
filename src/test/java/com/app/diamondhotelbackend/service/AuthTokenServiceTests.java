package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.entity.AuthToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.repository.AuthTokenRepository;
import com.app.diamondhotelbackend.security.jwt.CustomUserDetails;
import com.app.diamondhotelbackend.service.authtoken.AuthTokenServiceImpl;
import com.app.diamondhotelbackend.service.jwt.JwtServiceImpl;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.util.Constant;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthTokenServiceTests {

    @InjectMocks
    private AuthTokenServiceImpl authTokenService;

    @Mock
    private AuthTokenRepository authTokenRepository;

    @Mock
    private UserProfileServiceImpl userProfileService;

    @Mock
    private JwtServiceImpl jwtService;

    private UserProfile userProfile;

    private UserDetails userDetails;

    private AuthToken authToken;

    private AuthToken updatedAuthToken;

    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60;

    @BeforeEach
    public void init() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        userProfile = UserProfile.builder()
                .id(1)
                .email("ala-gembala@wp.pl")
                .password(passwordEncoder.encode("#Test1111"))
                .passportNumber("ZF005401499")
                .role(Constant.USER)
                .authProvider(Constant.LOCAL)
                .accountConfirmed(true)
                .build();

        userDetails = new CustomUserDetails(
                userProfile.getId(),
                userProfile.getEmail(),
                userProfile.getPassword(),
                List.of(new SimpleGrantedAuthority(userProfile.getRole()))
        );

        authToken = AuthToken.builder()
                .id(1)
                .userProfile(userProfile)
                .accessValue("accessValue1")
                .refreshValue("refreshValue")
                .build();

        updatedAuthToken = AuthToken.builder()
                .id(1)
                .userProfile(userProfile)
                .accessValue("accessValue2")
                .refreshValue("refreshValue")
                .build();
    }

    @Test
    public void AuthTokenService_CreateAuthToken_ReturnsAuthToken() {
        when(userProfileService.getUserProfileByEmail(Mockito.any(String.class))).thenReturn(userProfile);
        when(jwtService.getAccessTokenExpiration()).thenReturn(ACCESS_TOKEN_EXPIRATION);
        when(authTokenRepository.save(Mockito.any(AuthToken.class))).thenReturn(authToken);

        AuthToken savedAuthToken = authTokenService.createAuthToken(userDetails);

        Assertions.assertThat(savedAuthToken).isNotNull();
        Assertions.assertThat(savedAuthToken.getId()).isEqualTo(1);
    }

    @Test
    public void AuthTokenService_UpdateAuthTokenAccessValue_ReturnsOptionalAuthToken() {
        when(jwtService.validateToken(Mockito.any(String.class))).thenReturn(Optional.of(userDetails));
        when(jwtService.getAccessTokenExpiration()).thenReturn(ACCESS_TOKEN_EXPIRATION);
        when(jwtService.createToken(Mockito.any(UserDetails.class), Mockito.any(long.class))).thenReturn(updatedAuthToken.getAccessValue());
        when(authTokenRepository.findByRefreshValue(Mockito.any(String.class))).thenReturn(Optional.of(authToken));
        when(authTokenRepository.save(Mockito.any(AuthToken.class))).thenReturn(updatedAuthToken);

        Optional<AuthToken> authTokenOptional = authTokenService.updateAuthTokenAccessValue(authToken.getRefreshValue());

        Assertions.assertThat(authTokenOptional).isPresent();
        Assertions.assertThat(authTokenOptional.get().getAccessValue()).isEqualTo(updatedAuthToken.getAccessValue());
    }
}
