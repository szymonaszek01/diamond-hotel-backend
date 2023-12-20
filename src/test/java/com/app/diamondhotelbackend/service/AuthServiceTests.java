package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.auth.request.*;
import com.app.diamondhotelbackend.dto.auth.response.AccountDetailsResponseDto;
import com.app.diamondhotelbackend.entity.AuthToken;
import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.security.jwt.CustomUserDetails;
import com.app.diamondhotelbackend.service.auth.AuthServiceImpl;
import com.app.diamondhotelbackend.service.authtoken.AuthTokenServiceImpl;
import com.app.diamondhotelbackend.service.confirmationtoken.ConfirmationTokenServiceImpl;
import com.app.diamondhotelbackend.service.email.EmailServiceImpl;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import org.assertj.core.api.Assertions;
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

    public void setPrincipal(Object principal) {
        this.principal = principal;
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
}

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {

    private static final long CONFIRMATION_TOKEN_EXPIRATION = 1000 * 60 * 15;
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
    private UserProfile userProfile;
    private UserProfile updatedUserProfile;
    private UserDetails userDetails;
    private Authentication authentication;
    private AuthToken authToken;
    private ConfirmationToken confirmationToken;
    private AccountLoginRequestDto accountLoginRequestDto;
    private AccountRegistrationRequestDto accountRegistrationRequestDto;
    private AccountEmailUpdateRequestDto accountEmailUpdateRequestDto;
    private AccountPasswordUpdateRequestDto accountPasswordUpdateRequestDto;
    private AccountForgottenPasswordRequestDto accountForgottenPasswordRequestDto;

    @BeforeEach
    public void init() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        userProfile = UserProfile.builder()
                .id(1)
                .email("ala-gembala@wp.pl")
                .password(passwordEncoder.encode("#Test1111"))
                .passportNumber("ZF005401499")
                .role(ConstantUtil.USER)
                .authProvider(ConstantUtil.LOCAL)
                .accountConfirmed(false)
                .build();

        updatedUserProfile = UserProfile.builder()
                .id(1)
                .passportNumber("ZF005401499")
                .role(ConstantUtil.USER)
                .authProvider(ConstantUtil.LOCAL)
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

        accountLoginRequestDto = AccountLoginRequestDto.builder()
                .email(userProfile.getEmail())
                .password(userProfile.getPassword())
                .build();

        accountRegistrationRequestDto = AccountRegistrationRequestDto.builder()
                .email(userProfile.getEmail())
                .password(userProfile.getPassword())
                .passportNumber(userProfile.getPassportNumber())
                .build();

        accountEmailUpdateRequestDto = AccountEmailUpdateRequestDto.builder()
                .email(userProfile.getEmail())
                .newEmail("tomek-bomek@gmail.com")
                .build();

        accountPasswordUpdateRequestDto = AccountPasswordUpdateRequestDto.builder()
                .email(userProfile.getEmail())
                .newPassword(passwordEncoder.encode("#Test2222"))
                .build();

        accountForgottenPasswordRequestDto = AccountForgottenPasswordRequestDto.builder()
                .token(confirmationToken.getAccessValue())
                .newPassword(passwordEncoder.encode("#Test2222"))
                .build();

        authentication = new Authentication();
    }

    @Test
    public void AuthService_LoginAccount_ReturnsUserProfileDetailsResponseDto() {
        userProfile.setAccountConfirmed(true);
        authentication.setPrincipal(userDetails);

        when(authenticationManager.authenticate(Mockito.any(org.springframework.security.core.Authentication.class))).thenReturn(authentication);
        when(authTokenService.createAuthToken(Mockito.any(UserDetails.class))).thenReturn(authToken);

        AccountDetailsResponseDto createdAccountDetailsResponseDto = authService.loginAccount(accountLoginRequestDto);

        Assertions.assertThat(createdAccountDetailsResponseDto).isNotNull();
        Assertions.assertThat(createdAccountDetailsResponseDto.getAccessToken()).isEqualTo(authToken.getAccessValue());
        Assertions.assertThat(createdAccountDetailsResponseDto.getRefreshToken()).isEqualTo(authToken.getRefreshValue());
        Assertions.assertThat(createdAccountDetailsResponseDto.getEmail()).isEqualTo(authToken.getUserProfile().getEmail());
        Assertions.assertThat(createdAccountDetailsResponseDto.isConfirmed()).isEqualTo(true);
    }

    @Test
    public void AuthService_RegisterAccount_ReturnsUserProfileDetailsResponseDto() {
        when(authenticationManager.authenticate(Mockito.any(org.springframework.security.core.Authentication.class))).thenReturn(authentication);
        when(userProfileService.createUserProfile(Mockito.any(UserProfile.class))).thenReturn(userProfile);
        when(confirmationTokenService.createConfirmationToken(Mockito.any(UserProfile.class))).thenReturn(confirmationToken);
        when(authTokenService.createAuthToken(Mockito.any(UserDetails.class))).thenReturn(authToken);

        AccountDetailsResponseDto createdAccountDetailsResponseDto = authService.registerAccount(accountRegistrationRequestDto);

        verify(emailService).sendConfirmationAccountEmail(Mockito.any(ConfirmationToken.class));

        Assertions.assertThat(createdAccountDetailsResponseDto).isNotNull();
        Assertions.assertThat(createdAccountDetailsResponseDto.getAccessToken()).isEqualTo(authToken.getAccessValue());
        Assertions.assertThat(createdAccountDetailsResponseDto.getRefreshToken()).isEqualTo(authToken.getRefreshValue());
        Assertions.assertThat(createdAccountDetailsResponseDto.getEmail()).isEqualTo(authToken.getUserProfile().getEmail());
        Assertions.assertThat(createdAccountDetailsResponseDto.isConfirmed()).isEqualTo(false);
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

        AccountDetailsResponseDto createdAccountDetailsResponseDto = authService.confirmAccount(confirmationToken.getAccessValue());

        Assertions.assertThat(createdAccountDetailsResponseDto).isNotNull();
        Assertions.assertThat(createdAccountDetailsResponseDto.getAccessToken()).isEqualTo(authToken.getAccessValue());
        Assertions.assertThat(createdAccountDetailsResponseDto.getRefreshToken()).isEqualTo(authToken.getRefreshValue());
        Assertions.assertThat(createdAccountDetailsResponseDto.getEmail()).isEqualTo(authToken.getUserProfile().getEmail());
        Assertions.assertThat(confirmationToken.getConfirmedAt()).isNotNull();
        Assertions.assertThat(createdAccountDetailsResponseDto.isConfirmed()).isEqualTo(true);
    }

    @Test
    public void AuthService_ForgotAccountPassword_ReturnsString() {
        userProfile.setAccountConfirmed(true);

        when(userProfileService.getUserProfileByEmail(Mockito.any(String.class))).thenReturn(userProfile);
        when(confirmationTokenService.createConfirmationToken(Mockito.any(UserProfile.class))).thenReturn(confirmationToken);

        String savedConfirmationToken = authService.forgotAccountPassword(userProfile.getEmail());

        verify(emailService).sendConfirmationPasswordChangingEmail(Mockito.any(ConfirmationToken.class));

        Assertions.assertThat(savedConfirmationToken).isNotNull();
        Assertions.assertThat(savedConfirmationToken).isEqualTo("Link was sent to " + userProfile.getEmail() + " successfully");
    }

    @Test
    public void AuthService_UpdateAccountEmail_ReturnsUserProfile() {
        userProfile.setAccountConfirmed(true);
        updatedUserProfile.setEmail(accountEmailUpdateRequestDto.getNewEmail());
        updatedUserProfile.setPassword(userProfile.getPassword());

        when(userProfileService.getUserProfileByEmail(Mockito.any(String.class))).thenReturn(userProfile);
        when(userProfileService.updateUserProfile(Mockito.any(UserProfile.class))).thenReturn(updatedUserProfile);
        when(confirmationTokenService.createConfirmationToken(Mockito.any(UserProfile.class))).thenReturn(confirmationToken);

        UserProfile savedUserProfile = authService.updateAccountEmail(accountEmailUpdateRequestDto);

        verify(emailService).sendConfirmationAccountEmail(Mockito.any(ConfirmationToken.class));

        Assertions.assertThat(savedUserProfile).isNotNull();
        Assertions.assertThat(savedUserProfile.getId()).isEqualTo(userProfile.getId());
        Assertions.assertThat(savedUserProfile.getEmail()).isEqualTo(accountEmailUpdateRequestDto.getNewEmail());
    }

    @Test
    public void AuthService_UpdateAccountPassword_ReturnsUserProfile_AcceptsForgotAccountPasswordRequestDto() {
        userProfile.setAccountConfirmed(true);
        updatedUserProfile.setEmail(userProfile.getEmail());
        updatedUserProfile.setPassword(accountForgottenPasswordRequestDto.getNewPassword());
        confirmationToken.setConfirmedAt(new Date(System.currentTimeMillis()));

        when(authenticationManager.authenticate(Mockito.any(org.springframework.security.core.Authentication.class))).thenReturn(authentication);
        when(userProfileService.updateUserProfile(Mockito.any(UserProfile.class))).thenReturn(updatedUserProfile);
        when(confirmationTokenService.updateConfirmationTokenConfirmedAt(Mockito.any(String.class))).thenReturn(confirmationToken);

        UserProfile savedUserProfile = authService.updateAccountPassword(accountForgottenPasswordRequestDto);

        Assertions.assertThat(savedUserProfile).isNotNull();
        Assertions.assertThat(savedUserProfile.getId()).isEqualTo(userProfile.getId());
        Assertions.assertThat(savedUserProfile.getPassword()).isEqualTo(accountForgottenPasswordRequestDto.getNewPassword());
    }

    @Test
    public void AuthService_UpdateAccountPassword_ReturnsUserProfile_AcceptsUpdateAccountPasswordRequestDto() {
        userProfile.setAccountConfirmed(true);
        updatedUserProfile.setEmail(userProfile.getEmail());
        updatedUserProfile.setPassword(accountPasswordUpdateRequestDto.getNewPassword());

        when(authenticationManager.authenticate(Mockito.any(org.springframework.security.core.Authentication.class))).thenReturn(authentication);
        when(userProfileService.getUserProfileByEmail(Mockito.any(String.class))).thenReturn(userProfile);
        when(userProfileService.updateUserProfile(Mockito.any(UserProfile.class))).thenReturn(updatedUserProfile);

        UserProfile savedUserProfile = authService.updateAccountPassword(accountPasswordUpdateRequestDto);

        Assertions.assertThat(savedUserProfile).isNotNull();
        Assertions.assertThat(savedUserProfile.getId()).isEqualTo(userProfile.getId());
        Assertions.assertThat(savedUserProfile.getPassword()).isEqualTo(accountPasswordUpdateRequestDto.getNewPassword());
    }

    @Test
    public void AuthService_UpdateAuthToken_ReturnsUserProfileDetailsResponseDto() {
        String token = authToken.getAccessValue();
        authToken.setAccessValue("accessValue2");

        when(authTokenService.updateAuthTokenAccessValue(Mockito.any(String.class))).thenReturn(Optional.of(authToken));

        AccountDetailsResponseDto createdAccountDetailsResponseDto = authService.updateAuthToken(token);

        Assertions.assertThat(createdAccountDetailsResponseDto).isNotNull();
        Assertions.assertThat(createdAccountDetailsResponseDto.getAccessToken()).isEqualTo(authToken.getAccessValue());
        Assertions.assertThat(createdAccountDetailsResponseDto.getRefreshToken()).isEqualTo(authToken.getRefreshValue());
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
