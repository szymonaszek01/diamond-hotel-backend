package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.auth.*;
import com.app.diamondhotelbackend.entity.AuthToken;
import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.security.jwt.CustomUserDetails;
import com.app.diamondhotelbackend.service.auth.AuthServiceImpl;
import com.app.diamondhotelbackend.service.authtoken.AuthTokenServiceImpl;
import com.app.diamondhotelbackend.service.confirmationtoken.ConfirmationTokenServiceImpl;
import com.app.diamondhotelbackend.service.email.EmailServiceImpl;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.util.Constant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class Authentication implements org.springframework.security.core.Authentication {

    private Object principal;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return false;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    }

    @Override
    public String getName() {
        return null;
    }

    public void setPrincipal(Object principal) {
        this.principal = principal;
    }
}

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserProfileServiceImpl userProfileService;

    @Mock
    private AuthTokenServiceImpl authTokenService;

    @Mock
    private ConfirmationTokenServiceImpl confirmationTokenService;

    @Mock
    private EmailServiceImpl emailService;

    private PasswordEncoder passwordEncoder;

    private UserProfile userProfile;

    private UserProfile updatedUserProfile;

    private UserDetails userDetails;

    private Authentication authentication;

    private AuthToken authToken;

    private ConfirmationToken confirmationToken;

    private LoginRequestDto loginRequestDto;

    private RegisterRequestDto registerRequestDto;

    private UpdateEmailRequestDto updateEmailRequestDto;

    private UpdatePasswordRequestDto updatePasswordRequestDto;

    private ChangePasswordRequestDto changePasswordRequestDto;

    private static final long CONFIRMATION_TOKEN_EXPIRATION = 1000 * 60 * 15;

    @BeforeEach
    public void init() {
        passwordEncoder = new BCryptPasswordEncoder();

        userProfile = UserProfile.builder()
                .id(1)
                .email("ala-gembala@wp.pl")
                .password(passwordEncoder.encode("#Test1111"))
                .passportNumber("ZF005401499")
                .role(Constant.USER)
                .authProvider(Constant.LOCAL)
                .accountConfirmed(false)
                .build();

        updatedUserProfile = UserProfile.builder()
                .id(1)
                .passportNumber("ZF005401499")
                .role(Constant.USER)
                .authProvider(Constant.LOCAL)
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

        confirmationToken = ConfirmationToken.builder()
                .id(1)
                .userProfile(userProfile)
                .accessValue("accessValue1")
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + CONFIRMATION_TOKEN_EXPIRATION))
                .build();

        loginRequestDto = LoginRequestDto.builder()
                .email(userProfile.getEmail())
                .password(userProfile.getPassword())
                .build();

        registerRequestDto = RegisterRequestDto.builder()
                .email(userProfile.getEmail())
                .password(userProfile.getPassword())
                .passportNumber(userProfile.getPassportNumber())
                .build();

        updateEmailRequestDto = UpdateEmailRequestDto.builder()
                .email(userProfile.getEmail())
                .newEmail("tomek-bomek@gmail.com")
                .build();

        updatePasswordRequestDto = UpdatePasswordRequestDto.builder()
                .email(userProfile.getEmail())
                .newPassword(passwordEncoder.encode("#Test2222"))
                .build();

        changePasswordRequestDto = ChangePasswordRequestDto.builder()
                .token(confirmationToken.getAccessValue())
                .newPassword(passwordEncoder.encode("#Test2222"))
                .build();

        authentication = new Authentication();
    }

    @AfterEach
    public void reinit() {
        userProfile = UserProfile.builder()
                .id(1)
                .email("ala-gembala@wp.pl")
                .password(passwordEncoder.encode("#Test1111"))
                .passportNumber("ZF005401499")
                .role(Constant.USER)
                .authProvider(Constant.LOCAL)
                .accountConfirmed(false)
                .build();

        updatedUserProfile = UserProfile.builder()
                .id(1)
                .passportNumber("ZF005401499")
                .role(Constant.USER)
                .authProvider(Constant.LOCAL)
                .build();

        authToken = AuthToken.builder()
                .id(1)
                .userProfile(userProfile)
                .accessValue("accessValue1")
                .refreshValue("refreshValue")
                .build();

        confirmationToken = ConfirmationToken.builder()
                .id(1)
                .userProfile(userProfile)
                .accessValue("accessValue1")
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + CONFIRMATION_TOKEN_EXPIRATION))
                .build();

        authentication.setPrincipal(null);
    }

    @Test
    public void AuthService_LoginAccount_ReturnsUserProfileDetailsResponseDto() {
        userProfile.setAccountConfirmed(true);
        authentication.setPrincipal(userDetails);

        when(authenticationManager.authenticate(Mockito.any(org.springframework.security.core.Authentication.class))).thenReturn(authentication);
        when(authTokenService.createAuthToken(Mockito.any(UserDetails.class))).thenReturn(authToken);

        UserProfileDetailsResponseDto createdUserProfileDetailsResponseDto = authService.loginAccount(loginRequestDto);

        Assertions.assertThat(createdUserProfileDetailsResponseDto).isNotNull();
        Assertions.assertThat(createdUserProfileDetailsResponseDto.getAccessToken()).isEqualTo(authToken.getAccessValue());
        Assertions.assertThat(createdUserProfileDetailsResponseDto.getRefreshToken()).isEqualTo(authToken.getRefreshValue());
        Assertions.assertThat(createdUserProfileDetailsResponseDto.getEmail()).isEqualTo(authToken.getUserProfile().getEmail());
        Assertions.assertThat(createdUserProfileDetailsResponseDto.isConfirmed()).isEqualTo(true);
    }

    @Test
    public void AuthService_RegisterAccount_ReturnsUserProfileDetailsResponseDto() {
        when(authenticationManager.authenticate(Mockito.any(org.springframework.security.core.Authentication.class))).thenReturn(authentication);
        when(userProfileService.createUserProfile(Mockito.any(UserProfile.class))).thenReturn(userProfile);
        when(confirmationTokenService.createConfirmationToken(Mockito.any(UserProfile.class))).thenReturn(confirmationToken);
        when(authTokenService.createAuthToken(Mockito.any(UserDetails.class))).thenReturn(authToken);

        UserProfileDetailsResponseDto createdUserProfileDetailsResponseDto = authService.registerAccount(registerRequestDto);

        verify(emailService).sendConfirmationAccountEmail(Mockito.any(ConfirmationToken.class));

        Assertions.assertThat(createdUserProfileDetailsResponseDto).isNotNull();
        Assertions.assertThat(createdUserProfileDetailsResponseDto.getAccessToken()).isEqualTo(authToken.getAccessValue());
        Assertions.assertThat(createdUserProfileDetailsResponseDto.getRefreshToken()).isEqualTo(authToken.getRefreshValue());
        Assertions.assertThat(createdUserProfileDetailsResponseDto.getEmail()).isEqualTo(authToken.getUserProfile().getEmail());
        Assertions.assertThat(createdUserProfileDetailsResponseDto.isConfirmed()).isEqualTo(false);
    }

    @Test
    public void AuthService_ConfirmAccount_ReturnsUserProfileDetailsResponseDto() {
        updatedUserProfile.setAccountConfirmed(true);
        updatedUserProfile.setEmail(userProfile.getEmail());
        updatedUserProfile.setPassword(userProfile.getPassword());
        confirmationToken.setConfirmedAt(new Date(System.currentTimeMillis()));

        when(confirmationTokenService.updateConfirmationTokenConfirmedAt((Mockito.any(String.class)))).thenReturn(confirmationToken);
        when(userProfileService.updateUserProfile(Mockito.any(UserProfile.class))).thenReturn(userProfile);
        when(authTokenService.createAuthToken(Mockito.any(UserDetails.class))).thenReturn(authToken);

        UserProfileDetailsResponseDto createdUserProfileDetailsResponseDto = authService.confirmAccount(confirmationToken.getAccessValue());

        Assertions.assertThat(createdUserProfileDetailsResponseDto).isNotNull();
        Assertions.assertThat(createdUserProfileDetailsResponseDto.getAccessToken()).isEqualTo(authToken.getAccessValue());
        Assertions.assertThat(createdUserProfileDetailsResponseDto.getRefreshToken()).isEqualTo(authToken.getRefreshValue());
        Assertions.assertThat(createdUserProfileDetailsResponseDto.getEmail()).isEqualTo(authToken.getUserProfile().getEmail());
        Assertions.assertThat(confirmationToken.getConfirmedAt()).isNotNull();
        Assertions.assertThat(createdUserProfileDetailsResponseDto.isConfirmed()).isEqualTo(true);
    }

    @Test
    public void AuthService_ForgotAccountPassword_ReturnsConfirmationToken() {
        userProfile.setAccountConfirmed(true);

        when(userProfileService.getUserProfileByEmail(Mockito.any(String.class))).thenReturn(userProfile);
        when(confirmationTokenService.createConfirmationToken(Mockito.any(UserProfile.class))).thenReturn(confirmationToken);

        ConfirmationToken savedConfirmationToken = authService.forgotAccountPassword(userProfile.getEmail());

        verify(emailService).sendConfirmationPasswordChangingEmail(Mockito.any(ConfirmationToken.class));

        Assertions.assertThat(savedConfirmationToken).isNotNull();
        Assertions.assertThat(savedConfirmationToken.getAccessValue()).isEqualTo(confirmationToken.getAccessValue());
        Assertions.assertThat(savedConfirmationToken.getConfirmedAt()).isNull();
    }

    @Test
    public void AuthService_UpdateAccountEmail_ReturnsUserProfile() {
        userProfile.setAccountConfirmed(true);
        updatedUserProfile.setEmail(updateEmailRequestDto.getNewEmail());
        updatedUserProfile.setPassword(userProfile.getPassword());

        when(userProfileService.getUserProfileByEmail(Mockito.any(String.class))).thenReturn(userProfile);
        when(userProfileService.updateUserProfile(Mockito.any(UserProfile.class))).thenReturn(updatedUserProfile);
        when(confirmationTokenService.createConfirmationToken(Mockito.any(UserProfile.class))).thenReturn(confirmationToken);

        UserProfile savedUserProfile = authService.updateAccountEmail(updateEmailRequestDto);

        verify(emailService).sendConfirmationAccountEmail(Mockito.any(ConfirmationToken.class));

        Assertions.assertThat(savedUserProfile).isNotNull();
        Assertions.assertThat(savedUserProfile.getId()).isEqualTo(userProfile.getId());
        Assertions.assertThat(savedUserProfile.getEmail()).isEqualTo(updateEmailRequestDto.getNewEmail());
    }

    @Test
    public void AuthService_UpdateAccountPassword_ReturnsUserProfile_AcceptsChangePasswordRequestDto() {
        userProfile.setAccountConfirmed(true);
        updatedUserProfile.setEmail(userProfile.getEmail());
        updatedUserProfile.setPassword(changePasswordRequestDto.getNewPassword());
        confirmationToken.setConfirmedAt(new Date(System.currentTimeMillis()));

        when(userProfileService.updateUserProfile(Mockito.any(UserProfile.class))).thenReturn(updatedUserProfile);
        when(confirmationTokenService.updateConfirmationTokenConfirmedAt(Mockito.any(String.class))).thenReturn(confirmationToken);

        UserProfile savedUserProfile = authService.updateAccountPassword(changePasswordRequestDto);

        Assertions.assertThat(savedUserProfile).isNotNull();
        Assertions.assertThat(savedUserProfile.getId()).isEqualTo(userProfile.getId());
        Assertions.assertThat(savedUserProfile.getPassword()).isEqualTo(changePasswordRequestDto.getNewPassword());
    }

    @Test
    public void AuthService_UpdateAccountPassword_ReturnsUserProfile_AcceptsUpdatePasswordRequestDto() {
        userProfile.setAccountConfirmed(true);
        updatedUserProfile.setEmail(userProfile.getEmail());
        updatedUserProfile.setPassword(updatePasswordRequestDto.getNewPassword());

        when(userProfileService.getUserProfileByEmail(Mockito.any(String.class))).thenReturn(userProfile);
        when(userProfileService.updateUserProfile(Mockito.any(UserProfile.class))).thenReturn(updatedUserProfile);

        UserProfile savedUserProfile = authService.updateAccountPassword(updatePasswordRequestDto);

        Assertions.assertThat(savedUserProfile).isNotNull();
        Assertions.assertThat(savedUserProfile.getId()).isEqualTo(userProfile.getId());
        Assertions.assertThat(savedUserProfile.getPassword()).isEqualTo(updatePasswordRequestDto.getNewPassword());
    }

    @Test
    public void AuthService_UpdateAuthToken_ReturnsUserProfileDetailsResponseDto() {
        String token = authToken.getAccessValue();
        authToken.setAccessValue("accessValue2");

        when(authTokenService.updateAuthTokenAccessValue(Mockito.any(String.class))).thenReturn(Optional.of(authToken));

        UserProfileDetailsResponseDto createdUserProfileDetailsResponseDto = authService.updateAuthToken(token);

        Assertions.assertThat(createdUserProfileDetailsResponseDto).isNotNull();
        Assertions.assertThat(createdUserProfileDetailsResponseDto.getAccessToken()).isEqualTo(authToken.getAccessValue());
        Assertions.assertThat(createdUserProfileDetailsResponseDto.getRefreshToken()).isEqualTo(authToken.getRefreshValue());
    }

    @Test
    public void AuthService_UpdateConfirmationToken_ReturnsConfirmationToken() {
        String token = confirmationToken.getAccessValue();
        confirmationToken.setAccessValue("accessValue2");

        when(confirmationTokenService.updateConfirmationToken(Mockito.any(String.class))).thenReturn(confirmationToken);

        ConfirmationToken savedConfirmationToken = authService.updateConfirmationToken(token);

        verify(emailService).sendConfirmationAccountEmail(Mockito.any(ConfirmationToken.class));

        Assertions.assertThat(savedConfirmationToken).isNotNull();
        Assertions.assertThat(savedConfirmationToken.getId()).isEqualTo(confirmationToken.getId());
        Assertions.assertThat(savedConfirmationToken.getAccessValue()).isEqualTo(confirmationToken.getAccessValue());
    }
}
