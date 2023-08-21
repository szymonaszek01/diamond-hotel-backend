package com.app.diamondhotelbackend.service.auth;

import com.app.diamondhotelbackend.dto.auth.*;
import com.app.diamondhotelbackend.exception.AuthProcessingException;
import com.app.diamondhotelbackend.exception.ConfirmationTokenProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;

public interface AuthService {

    UserProfileDetailsResponseDto login(LoginRequestDto loginRequestDto) throws AuthProcessingException, UserProfileProcessingException;

    UserProfileDetailsResponseDto register(RegisterRequestDto registerRequestDto) throws AuthProcessingException, UserProfileProcessingException;

    UserProfileDetailsResponseDto confirmAccount(String token) throws ConfirmationTokenProcessingException;

    UserProfileDetailsResponseDto refreshAuthToken(String expiredToken) throws AuthProcessingException;

    void refreshConfirmationToken(String expiredToken) throws UserProfileProcessingException, ConfirmationTokenProcessingException;

    void confirmChangingPassword(String email) throws UserProfileProcessingException;

    void updateEmail(UpdateEmailRequestDto updateEmailRequestDto);

    void updatePassword(ChangePasswordRequestDto changePasswordRequestDto) throws AuthProcessingException, ConfirmationTokenProcessingException;

    void updatePassword(UpdatePasswordRequestDto updatePasswordRequestDto) throws AuthProcessingException, UserProfileProcessingException;
}
