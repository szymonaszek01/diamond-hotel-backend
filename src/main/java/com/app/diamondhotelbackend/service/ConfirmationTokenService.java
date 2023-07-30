package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.ConfirmationTokenProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.repository.ConfirmationTokenRepository;
import com.app.diamondhotelbackend.util.Constant;
import com.app.diamondhotelbackend.util.JwtPropertiesProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    private final UserProfileService userProfileService;

    private final JwtPropertiesProvider jwtPropertiesProvider;

    public ConfirmationToken createConfirmationToken(UserProfile userProfile) {
        String accessValue = UUID.randomUUID().toString();

        return ConfirmationToken.builder()
                .accessValue(accessValue)
                .userProfile(userProfile)
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + Long.parseLong(jwtPropertiesProvider.getConfirmationTokenExpiration())))
                .build();
    }

    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }

    public UserProfile updateConfirmedAt(String token) throws ConfirmationTokenProcessingException {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findConfirmationTokenByAccessValue(token).orElseThrow(() -> new ConfirmationTokenProcessingException(Constant.CONFIRMATION_TOKEN_NOT_FOUND));
        if (confirmationToken.getConfirmedAt() != null) {
            throw new ConfirmationTokenProcessingException(Constant.CONFIRMATION_TOKEN_ALREADY_CONFIRMED);
        }
        if (confirmationToken.getExpiresAt().before(new Date(System.currentTimeMillis()))) {
            throw new ConfirmationTokenProcessingException(Constant.CONFIRMATION_TOKEN_ALREADY_EXPIRED);
        }

        confirmationToken.setConfirmedAt(new Date(System.currentTimeMillis()));
        confirmationTokenRepository.save(confirmationToken);

        return confirmationToken.getUserProfile();
    }

    public ConfirmationToken updateTokenToEmailConfirmation(long userId) throws UserProfileProcessingException, ConfirmationTokenProcessingException {
        UserProfile userProfile = userProfileService.getUserProfileById(userId);
        ConfirmationToken confirmationToken = confirmationTokenRepository.findConfirmationTokenByUserProfile(userProfile).orElseThrow(() -> new ConfirmationTokenProcessingException(Constant.CONFIRMATION_TOKEN_NOT_FOUND));
        if (confirmationToken.getConfirmedAt() != null) {
            throw new ConfirmationTokenProcessingException(Constant.CONFIRMATION_TOKEN_ALREADY_CONFIRMED);
        }

        confirmationToken.setAccessValue(UUID.randomUUID().toString());
        confirmationToken.setCreatedAt(new Date(System.currentTimeMillis()));
        confirmationToken.setExpiresAt(new Date(System.currentTimeMillis() + Long.parseLong(jwtPropertiesProvider.getConfirmationTokenExpiration())));

        return confirmationTokenRepository.save(confirmationToken);
    }
}
