package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.service.email.EmailServiceImpl;
import com.app.diamondhotelbackend.util.ApplicationPropertiesUtil;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.EmailUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTests {

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private ApplicationPropertiesUtil applicationPropertiesUtil;

    @Mock
    private EmailUtil emailUtil;

    private ConfirmationToken confirmationToken;

    @BeforeEach
    public void init() {
        long expiration = 1000 * 60 * 15;

        UserProfile userProfile = UserProfile.builder()
                .email("ala-gembala@wp.pl")
                .passportNumber("ZF005401499")
                .role(ConstantUtil.USER)
                .authProvider(ConstantUtil.LOCAL)
                .accountConfirmed(false)
                .build();

        confirmationToken = ConfirmationToken.builder()
                .userProfile(userProfile)
                .accessValue("accessValue")
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + expiration))
                .build();
    }

    @Test
    public void EmailService_SendConfirmationAccountEmail_ReturnsNothing() {
        when(applicationPropertiesUtil.getClientUri()).thenReturn("http://localhost:3000");

        emailService.sendConfirmationAccountEmail(confirmationToken);

        verify(emailUtil, timeout(2000)).send(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class));
    }

    @Test
    public void EmailService_SendChangingPasswordEmail_ReturnsNothing() {
        when(applicationPropertiesUtil.getClientUri()).thenReturn("http://localhost:3000");

        emailService.sendConfirmationPasswordChangingEmail(confirmationToken);

        verify(emailUtil, timeout(2000)).send(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class));
    }
}
