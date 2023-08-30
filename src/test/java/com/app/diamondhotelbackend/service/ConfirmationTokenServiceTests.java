package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.repository.ConfirmationTokenRepository;
import com.app.diamondhotelbackend.service.confirmationtoken.ConfirmationTokenServiceImpl;
import com.app.diamondhotelbackend.util.ApplicationPropertiesUtil;
import com.app.diamondhotelbackend.util.ConstantUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConfirmationTokenServiceTests {

    @InjectMocks
    private ConfirmationTokenServiceImpl confirmationTokenService;

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Mock
    private ApplicationPropertiesUtil applicationPropertiesUtil;

    private UserProfile userProfile;

    private ConfirmationToken confirmationToken;

    private ConfirmationToken updatedConfirmationToken;

    private ConfirmationToken confirmedConfirmationToken;

    private static final long CONFIRMATION_TOKEN_EXPIRATION = 1000 * 60 * 15;

    @BeforeEach
    public void init() {
        userProfile = UserProfile.builder()
                .id(1)
                .email("ala-gembala@wp.pl")
                .passportNumber("ZF005401499")
                .role(ConstantUtil.USER)
                .authProvider(ConstantUtil.LOCAL)
                .accountConfirmed(false)
                .build();

        confirmationToken = ConfirmationToken.builder()
                .id(1)
                .userProfile(userProfile)
                .accessValue("accessValue1")
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + CONFIRMATION_TOKEN_EXPIRATION))
                .build();

        updatedConfirmationToken = ConfirmationToken.builder()
                .id(1)
                .userProfile(userProfile)
                .accessValue("accessValue2")
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + CONFIRMATION_TOKEN_EXPIRATION))
                .build();

        confirmedConfirmationToken = ConfirmationToken.builder()
                .id(1)
                .userProfile(userProfile)
                .accessValue("accessValue1")
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + CONFIRMATION_TOKEN_EXPIRATION))
                .confirmedAt(new Date(System.currentTimeMillis() + 1000 * 60 * 5))
                .build();
    }

    @Test
    public void ConfirmationTokenService_CreateConfirmationToken_ReturnsConfirmationToken() {
        when(applicationPropertiesUtil.getConfirmationTokenExpiration()).thenReturn(String.valueOf(CONFIRMATION_TOKEN_EXPIRATION));
        when(confirmationTokenRepository.save(Mockito.any(ConfirmationToken.class))).thenReturn(confirmationToken);

        ConfirmationToken savedConfirmationToken = confirmationTokenService.createConfirmationToken(userProfile);

        Assertions.assertThat(savedConfirmationToken).isNotNull();
        Assertions.assertThat(savedConfirmationToken.getId()).isEqualTo(1);
    }

    @Test
    public void ConfirmationTokenService_UpdateConfirmationTokenConfirmedAt_ReturnsConfirmationToken() {
        when(applicationPropertiesUtil.getConfirmationTokenExpiration()).thenReturn(String.valueOf(CONFIRMATION_TOKEN_EXPIRATION));
        when(confirmationTokenRepository.findByAccessValue(Mockito.any(String.class))).thenReturn(Optional.of(confirmationToken));
        when(confirmationTokenRepository.save(Mockito.any(ConfirmationToken.class))).thenReturn(updatedConfirmationToken);

        ConfirmationToken savedConfirmationToken = confirmationTokenService.updateConfirmationToken(confirmationToken.getAccessValue());

        Assertions.assertThat(savedConfirmationToken).isNotNull();
        Assertions.assertThat(savedConfirmationToken.getId()).isEqualTo(1);
    }

    @Test
    public void ConfirmationTokenService_UpdateConfirmationTokenExpiredAt_ReturnsConfirmationToken() {
        when(applicationPropertiesUtil.getConfirmationTokenExpiration()).thenReturn(String.valueOf(CONFIRMATION_TOKEN_EXPIRATION));
        when(confirmationTokenRepository.findByAccessValue(Mockito.any(String.class))).thenReturn(Optional.of(confirmationToken));
        when(confirmationTokenRepository.save(Mockito.any(ConfirmationToken.class))).thenReturn(confirmedConfirmationToken);

        ConfirmationToken savedConfirmationToken = confirmationTokenService.updateConfirmationToken(confirmationToken.getAccessValue());

        Assertions.assertThat(savedConfirmationToken).isNotNull();
        Assertions.assertThat(savedConfirmationToken.getConfirmedAt()).isNotNull();
        Assertions.assertThat(savedConfirmationToken.getConfirmedAt()).isBefore(savedConfirmationToken.getExpiresAt());
    }
}
