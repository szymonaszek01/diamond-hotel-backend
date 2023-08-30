package com.app.diamondhotelbackend.service.confirmationtoken;

import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.ConfirmationTokenProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.repository.ConfirmationTokenRepository;
import com.app.diamondhotelbackend.util.ApplicationPropertiesUtil;
import com.app.diamondhotelbackend.util.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    private final ApplicationPropertiesUtil applicationPropertiesUtil;

    @Override
    public ConfirmationToken createConfirmationToken(UserProfile userProfile) {
        String accessValue = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .accessValue(accessValue)
                .userProfile(userProfile)
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + Long.parseLong(applicationPropertiesUtil.getConfirmationTokenExpiration())))
                .build();

        return confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    public ConfirmationToken updateConfirmationToken(String expiredToken) throws UserProfileProcessingException, ConfirmationTokenProcessingException {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByAccessValue(expiredToken).orElseThrow(() -> new ConfirmationTokenProcessingException(ConstantUtil.CONFIRMATION_TOKEN_NOT_FOUND));
        if (confirmationToken.getConfirmedAt() != null) {
            throw new ConfirmationTokenProcessingException(ConstantUtil.CONFIRMATION_TOKEN_ALREADY_CONFIRMED);
        }

        confirmationToken.setAccessValue(UUID.randomUUID().toString());
        confirmationToken.setCreatedAt(new Date(System.currentTimeMillis()));
        confirmationToken.setExpiresAt(new Date(System.currentTimeMillis() + Long.parseLong(applicationPropertiesUtil.getConfirmationTokenExpiration())));

        return confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    public ConfirmationToken updateConfirmationTokenConfirmedAt(String token) throws ConfirmationTokenProcessingException {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByAccessValue(token).orElseThrow(() -> new ConfirmationTokenProcessingException(ConstantUtil.CONFIRMATION_TOKEN_NOT_FOUND));
        if (confirmationToken.getConfirmedAt() != null) {
            throw new ConfirmationTokenProcessingException(ConstantUtil.CONFIRMATION_TOKEN_ALREADY_CONFIRMED);
        }
        if (confirmationToken.getExpiresAt().before(new Date(System.currentTimeMillis()))) {
            throw new ConfirmationTokenProcessingException(ConstantUtil.CONFIRMATION_TOKEN_ALREADY_EXPIRED);
        }

        confirmationToken.setConfirmedAt(new Date(System.currentTimeMillis()));

        return confirmationTokenRepository.save(confirmationToken);
    }
}
