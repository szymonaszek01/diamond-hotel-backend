package com.app.diamondhotelbackend.service.authtoken;

import com.app.diamondhotelbackend.entity.AuthToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.AuthProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.repository.AuthTokenRepository;
import com.app.diamondhotelbackend.service.jwt.JwtServiceImpl;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class AuthTokenServiceImpl implements AuthTokenService {

    private final AuthTokenRepository authTokenRepository;

    private final UserProfileServiceImpl userProfileService;

    private final JwtServiceImpl jwtService;

    @Override
    public AuthToken createAuthToken(UserDetails userDetails) throws UserProfileProcessingException {
        UserProfile userProfile = userProfileService.getUserProfileByEmail(userDetails.getUsername());
        Optional<AuthToken> token = authTokenRepository.findByUserProfile(userProfile);
        if (token.isPresent()) {
            token.get().setAccessValue(jwtService.createToken(userDetails, jwtService.getAccessTokenExpiration()));
            token.get().setRefreshValue(jwtService.createToken(userDetails, jwtService.getRefreshTokenExpiration()));
        } else {
            token = Optional.of(AuthToken.builder()
                    .userProfile(userProfile)
                    .accessValue(jwtService.createToken(userDetails, jwtService.getAccessTokenExpiration()))
                    .refreshValue(jwtService.createToken(userDetails, jwtService.getRefreshTokenExpiration()))
                    .build());
        }

        return authTokenRepository.save(token.get());
    }

    @Override
    public AuthToken getAuthTokenByAccessValue(String authToken) throws AuthProcessingException {
        return authTokenRepository.findByAccessValue(authToken).orElseThrow(() -> new AuthProcessingException(ConstantUtil.INVALID_TOKEN_EXCEPTION));
    }

    @Override
    public Optional<AuthToken> updateAuthTokenAccessValue(String refreshToken) {
        Optional<AuthToken> token = authTokenRepository.findByRefreshValue(refreshToken);
        if (token.isEmpty()) {
            return Optional.empty();
        }

        Optional<UserDetails> userDetails = jwtService.validateToken(refreshToken);
        if (userDetails.isEmpty()) {
            authTokenRepository.delete(token.get());
            return Optional.empty();
        }

        token.get().setAccessValue(jwtService.createToken(userDetails.get(), jwtService.getAccessTokenExpiration()));
        return Optional.of(authTokenRepository.save(token.get()));
    }
}
