package com.app.diamondhotelbackend.service.confirmationtoken;

import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.ConfirmationTokenProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;

public interface ConfirmationTokenService {
    ConfirmationToken createConfirmationToken(UserProfile userProfile);

    void saveConfirmationToken(ConfirmationToken confirmationToken);

    UserProfile updateConfirmedAt(String token) throws ConfirmationTokenProcessingException;

    ConfirmationToken refreshConfirmationToken(String expiredToken) throws UserProfileProcessingException, ConfirmationTokenProcessingException;
}
