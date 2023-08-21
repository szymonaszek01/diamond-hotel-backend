package com.app.diamondhotelbackend.service.auth;

import com.app.diamondhotelbackend.dto.auth.*;
import com.app.diamondhotelbackend.entity.AuthToken;
import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.AuthProcessingException;
import com.app.diamondhotelbackend.exception.ConfirmationTokenProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.security.jwt.CustomUserDetails;
import com.app.diamondhotelbackend.service.email.EmailServiceImpl;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.service.authtoken.AuthTokenServiceImpl;
import com.app.diamondhotelbackend.service.confirmationtoken.ConfirmationTokenService;
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
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserProfileServiceImpl userProfileService;

    private final AuthTokenServiceImpl authTokenService;

    private final ConfirmationTokenService confirmationTokenService;

    private final EmailServiceImpl emailService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserProfileDetailsResponseDto login(LoginRequestDto loginRequestDto) throws AuthProcessingException, UserProfileProcessingException {
        Optional<UserDetails> optionalUserDetails = getUserDetails(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        if (optionalUserDetails.isEmpty()) {
            throw new AuthProcessingException(Constant.USER_PROFILE_NOT_FOUND_EXCEPTION);
        }

        return createUserProfileDetailsResponseDto(optionalUserDetails.get());
    }

    @Override
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

    @Override
    public UserProfileDetailsResponseDto confirmAccount(String token) throws ConfirmationTokenProcessingException {
        String decodedToken = UrlUtil.decode(token);
        UserProfile userProfile = confirmationTokenService.updateConfirmedAt(decodedToken);
        userProfile.setAccountConfirmed(true);
        userProfileService.saveUserProfile(userProfile);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(userProfile.getRole()));
        CustomUserDetails customUserDetails = new CustomUserDetails(userProfile.getId(), userProfile.getEmail(), userProfile.getPassword(), authorities);

        return createUserProfileDetailsResponseDto(customUserDetails);
    }

    @Override
    public UserProfileDetailsResponseDto refreshAuthToken(String expiredToken) throws AuthProcessingException {
        return authTokenService.refreshAccessToken(UrlUtil.decode(expiredToken))
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

    @Override
    public void refreshConfirmationToken(String expiredToken) throws UserProfileProcessingException, ConfirmationTokenProcessingException {
        String decodedExpiredToken = UrlUtil.decode(expiredToken);
        ConfirmationToken confirmationToken = confirmationTokenService.refreshConfirmationToken(decodedExpiredToken);
        emailService.sendConfirmationAccountEmail(confirmationToken);
    }

    @Override
    public void confirmChangingPassword(String email) throws UserProfileProcessingException {
        String decodedEmail = UrlUtil.decode(email);
        UserProfile userProfile = userProfileService.getUserProfileByEmail(decodedEmail);
        ConfirmationToken confirmationToken = confirmationTokenService.createConfirmationToken(userProfile);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        emailService.sendChangingPasswordEmail(confirmationToken);
    }

    @Override
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

    @Override
    public void updatePassword(ChangePasswordRequestDto changePasswordRequestDto) throws AuthProcessingException, ConfirmationTokenProcessingException {
        UserProfile userProfile = confirmationTokenService.updateConfirmedAt(changePasswordRequestDto.getToken());
        verifyAndChangePassword(userProfile, changePasswordRequestDto.getNewPassword());
    }

    @Override
    public void updatePassword(UpdatePasswordRequestDto updatePasswordRequestDto) throws AuthProcessingException, UserProfileProcessingException {
        UserProfile userProfile = userProfileService.getUserProfileByEmail(updatePasswordRequestDto.getEmail());
        verifyAndChangePassword(userProfile, updatePasswordRequestDto.getNewPassword());
    }

    private UserProfileDetailsResponseDto createUserProfileDetailsResponseDto(UserDetails userDetails) throws UserProfileProcessingException {
        AuthToken authToken = authTokenService.saveToken(userDetails);
        return UserProfileDetailsResponseDto.builder()
                .accessToken(authToken.getAccessValue())
                .refreshToken(authToken.getRefreshValue())
                .email(authToken.getUserProfile().getEmail())
                .id(authToken.getUserProfile().getId())
                .confirmed(authToken.getUserProfile().isAccountConfirmed())
                .build();
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
