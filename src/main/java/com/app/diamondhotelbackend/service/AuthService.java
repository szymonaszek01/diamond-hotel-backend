package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.auth.*;
import com.app.diamondhotelbackend.entity.AuthToken;
import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.AuthProcessingException;
import com.app.diamondhotelbackend.exception.ConfirmationTokenProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.security.jwt.CustomUserDetails;
import com.app.diamondhotelbackend.util.Constant;
import com.app.diamondhotelbackend.util.UrlUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserProfileService userProfileService;

    private final AuthTokenService authTokenService;

    private final ConfirmationTokenService confirmationTokenService;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UrlUtil urlUtil;

    public UserProfileDetailsResponseDto login(LoginRequestDto loginRequestDto) throws AuthProcessingException, UserProfileProcessingException {
        Optional<UserDetails> optionalUserDetails = getUserDetails(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        if (optionalUserDetails.isEmpty()) {
            throw new AuthProcessingException(Constant.USER_PROFILE_NOT_FOUND_EXCEPTION);
        }

        return createUserProfileDetailsResponseDto(optionalUserDetails.get());
    }

    public UserProfileDetailsResponseDto register(RegisterRequestDto registerRequestDto) throws AuthProcessingException, UserProfileProcessingException {
        Optional<UserDetails> optionalUserDetails = getUserDetails(registerRequestDto.getEmail(), registerRequestDto.getPassword());
        if (optionalUserDetails.isPresent()) {
            throw new AuthProcessingException(Constant.USER_PROFILE_EXISTS_EXCEPTION);
        }

        UserProfile userProfile = userProfileService.saveUserProfile(registerRequestDto);
        ConfirmationToken confirmationToken = confirmationTokenService.createConfirmationToken(userProfile);
        emailService.sendConfirmationAccountEmail(confirmationToken);
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(userProfile.getRole()));
        return createUserProfileDetailsResponseDto(new CustomUserDetails(userProfile.getId(), userProfile.getEmail(), userProfile.getPassword(), authorities));
    }

    public UserProfileDetailsResponseDto confirmAccount(String token) throws ConfirmationTokenProcessingException {
        String decodedToken = urlUtil.decode(token);
        UserProfile userProfile = confirmationTokenService.updateConfirmedAt(decodedToken);
        userProfile.setAccountConfirmed(true);
        userProfileService.saveUserProfile(userProfile);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(userProfile.getRole()));
        CustomUserDetails customUserDetails = new CustomUserDetails(userProfile.getId(), userProfile.getEmail(), userProfile.getPassword(), authorities);

        return createUserProfileDetailsResponseDto(customUserDetails);
    }

    public UserProfileDetailsResponseDto refreshAuthToken(String expiredToken) throws AuthProcessingException {
        return authTokenService.refreshAccessToken(urlUtil.decode(expiredToken))
                .map(token -> UserProfileDetailsResponseDto.builder()
                        .accessToken(token.getAccessValue())
                        .refreshToken(token.getRefreshValue())
                        .email(token.getUserProfile().getEmail())
                        .id(token.getUserProfile().getId())
                        .confirmed(token.getUserProfile().isAccountConfirmed())
                        .build()
                )
                .orElseThrow(() -> new AuthProcessingException(Constant.INVALID_TOKEN_EXCEPTION));
    }

    public void refreshConfirmationToken(String expiredToken) throws UserProfileProcessingException, ConfirmationTokenProcessingException {
        String decodedExpiredToken = urlUtil.decode(expiredToken);
        ConfirmationToken confirmationToken = confirmationTokenService.refreshConfirmationToken(decodedExpiredToken);
        emailService.sendConfirmationAccountEmail(confirmationToken);
    }

    public UserProfileDetailsResponseDto createUserProfileDetailsResponseDto(UserDetails userDetails) throws UserProfileProcessingException {
        AuthToken authToken = authTokenService.saveToken(userDetails);
        return UserProfileDetailsResponseDto.builder()
                .accessToken(authToken.getAccessValue())
                .refreshToken(authToken.getRefreshValue())
                .email(authToken.getUserProfile().getEmail())
                .id(authToken.getUserProfile().getId())
                .confirmed(authToken.getUserProfile().isAccountConfirmed())
                .build();
    }

    public void confirmChangingPassword(String email) throws UserProfileProcessingException {
        String decodedEmail = urlUtil.decode(email);
        UserProfile userProfile = userProfileService.getUserProfileByEmail(decodedEmail);
        ConfirmationToken confirmationToken = confirmationTokenService.createConfirmationToken(userProfile);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        emailService.sendChangingPasswordEmail(confirmationToken);
    }

    public void updateEmail(UpdateEmailRequestDto updateEmailRequestDto) {
        UserProfile userProfile = userProfileService.getUserProfileByEmail(updateEmailRequestDto.getEmail());
        if (Constant.OAUTH2.equals(userProfile.getAuthProvider())) {
            throw new AuthProcessingException(Constant.INVALID_AUTH_PROVIDER_EXCEPTION);
        }
        if (!userProfileService.isNewEmailUnique(updateEmailRequestDto.getNewEmail())) {
            throw new AuthProcessingException(Constant.EMAIL_EXISTS_EXCEPTION);
        }

        userProfile.setEmail(updateEmailRequestDto.getNewEmail());
        userProfile.setAccountConfirmed(false);
        ConfirmationToken confirmationToken = confirmationTokenService.createConfirmationToken(userProfile);

        confirmationTokenService.saveConfirmationToken(confirmationToken);
        emailService.sendConfirmationAccountEmail(confirmationToken);
        userProfileService.saveUserProfile(userProfile);
    }

    public void updatePassword(ChangePasswordRequestDto changePasswordRequestDto) throws AuthProcessingException, ConfirmationTokenProcessingException {
        UserProfile userProfile = confirmationTokenService.updateConfirmedAt(changePasswordRequestDto.getToken());
        verifyAndChangePassword(userProfile, changePasswordRequestDto.getNewPassword());
    }

    public void updatePassword(UpdatePasswordRequestDto updatePasswordRequestDto) throws AuthProcessingException, UserProfileProcessingException {
        UserProfile userProfile = userProfileService.getUserProfileByEmail(updatePasswordRequestDto.getEmail());
        verifyAndChangePassword(userProfile, updatePasswordRequestDto.getNewPassword());
    }

    private void verifyAndChangePassword(UserProfile userProfile, String newPassword) throws AuthProcessingException {
        if (Constant.OAUTH2.equals(userProfile.getAuthProvider())) {
            throw new AuthProcessingException(Constant.INVALID_AUTH_PROVIDER_EXCEPTION);
        }
        if (!userProfileService.isNewPasswordUnique(newPassword)) {
            throw new AuthProcessingException(Constant.PASSWORD_EXISTS_EXCEPTION);
        }

        userProfile.setPassword(passwordEncoder.encode(newPassword));
        userProfileService.saveUserProfile(userProfile);
    }

    private Optional<UserDetails> getUserDetails(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            return Optional.of(userDetails);

        } catch (Exception e) {
            log.error(e.getMessage());

            return Optional.empty();
        }
    }
}
