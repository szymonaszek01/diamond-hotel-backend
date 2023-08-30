package com.app.diamondhotelbackend.service.auth;

import com.app.diamondhotelbackend.dto.auth.request.*;
import com.app.diamondhotelbackend.dto.auth.response.AccountDetailsResponseDto;
import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.AuthProcessingException;
import com.app.diamondhotelbackend.exception.ConfirmationTokenProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;

public interface AuthService {

    AccountDetailsResponseDto loginAccount(AccountLoginRequestDto accountLoginRequestDto) throws AuthProcessingException, UserProfileProcessingException;

    AccountDetailsResponseDto registerAccount(AccountRegistrationRequestDto accountRegistrationRequestDto) throws AuthProcessingException, UserProfileProcessingException;

    AccountDetailsResponseDto confirmAccount(String token) throws ConfirmationTokenProcessingException;

    ConfirmationToken forgotAccountPassword(String email) throws UserProfileProcessingException;

    UserProfile updateAccountEmail(AccountEmailUpdateRequestDto accountEmailUpdateRequestDto);

    UserProfile updateAccountPassword(AccountForgottenPasswordRequestDto accountForgottenPasswordRequestDto) throws AuthProcessingException, ConfirmationTokenProcessingException;

    UserProfile updateAccountPassword(AccountPasswordUpdateRequestDto accountPasswordUpdateRequestDto) throws AuthProcessingException, UserProfileProcessingException;

    AccountDetailsResponseDto updateAuthToken(String expiredToken) throws AuthProcessingException;

    ConfirmationToken updateConfirmationToken(String expiredToken) throws UserProfileProcessingException, ConfirmationTokenProcessingException;
}
