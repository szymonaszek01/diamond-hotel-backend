package com.app.diamondhotelbackend.service.auth;

import com.app.diamondhotelbackend.dto.auth.*;
import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.AuthProcessingException;
import com.app.diamondhotelbackend.exception.ConfirmationTokenProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;

public interface AuthService {

    UserProfileDetailsResponseDto loginAccount(LoginRequestDto loginRequestDto) throws AuthProcessingException, UserProfileProcessingException;

    UserProfileDetailsResponseDto registerAccount(RegisterRequestDto registerRequestDto) throws AuthProcessingException, UserProfileProcessingException;

    UserProfileDetailsResponseDto confirmAccount(String token) throws ConfirmationTokenProcessingException;

    ConfirmationToken forgotAccountPassword(String email) throws UserProfileProcessingException;

    UserProfile updateAccountEmail(UpdateEmailRequestDto updateEmailRequestDto);

    UserProfile updateAccountPassword(ChangePasswordRequestDto changePasswordRequestDto) throws AuthProcessingException, ConfirmationTokenProcessingException;

    UserProfile updateAccountPassword(UpdatePasswordRequestDto updatePasswordRequestDto) throws AuthProcessingException, UserProfileProcessingException;

    UserProfileDetailsResponseDto updateAuthToken(String expiredToken) throws AuthProcessingException;

    ConfirmationToken updateConfirmationToken(String expiredToken) throws UserProfileProcessingException, ConfirmationTokenProcessingException;
}
