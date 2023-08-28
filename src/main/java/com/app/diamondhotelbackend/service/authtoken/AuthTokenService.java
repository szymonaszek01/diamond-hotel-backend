package com.app.diamondhotelbackend.service.authtoken;

import com.app.diamondhotelbackend.entity.AuthToken;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface AuthTokenService {

    AuthToken createAuthToken(UserDetails userDetails) throws UserProfileProcessingException;

    Optional<AuthToken> updateAuthTokenAccessValue(String refreshToken);
}
