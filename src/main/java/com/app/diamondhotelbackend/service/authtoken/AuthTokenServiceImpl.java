package com.app.diamondhotelbackend.service.authtoken;

import com.app.diamondhotelbackend.entity.AuthToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.repository.AuthTokenRepository;
import com.app.diamondhotelbackend.security.jwt.JwtProvider;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class AuthTokenServiceImpl implements AuthTokenService {

    private final UserProfileServiceImpl userProfileService;

    private final AuthTokenRepository authTokenRepository;

    private final JwtProvider jwtProvider;

    @Override
    public Optional<AuthToken> refreshAccessToken(String refreshToken) {
        Optional<AuthToken> token = authTokenRepository.findTokenByRefreshValue(refreshToken);
        if (token.isEmpty()) {
            return Optional.empty();
        }

        Optional<UserDetails> userDetails = jwtProvider.validateToken(refreshToken);
        if (userDetails.isEmpty()) {
            authTokenRepository.delete(token.get());
            return Optional.empty();
        }

        token.get().setAccessValue(jwtProvider.createToken(userDetails.get(), jwtProvider.getAccessTokenExpiration()));
        return Optional.of(authTokenRepository.save(token.get()));
    }

    @Override
    public AuthToken saveToken(UserDetails userDetails) throws UserProfileProcessingException {
        UserProfile userProfile = userProfileService.getUserProfileByEmail(userDetails.getUsername());
        Optional<AuthToken> token = authTokenRepository.findTokenByUserProfile(userProfile);
        if (token.isPresent()) {
            token.get().setAccessValue(jwtProvider.createToken(userDetails, jwtProvider.getAccessTokenExpiration()));
            token.get().setRefreshValue(jwtProvider.createToken(userDetails, jwtProvider.getRefreshTokenExpiration()));
        } else {
            token = Optional.of(AuthToken.builder()
                    .userProfile(userProfile)
                    .accessValue(jwtProvider.createToken(userDetails, jwtProvider.getAccessTokenExpiration()))
                    .refreshValue(jwtProvider.createToken(userDetails, jwtProvider.getRefreshTokenExpiration()))
                    .build());
        }

        return authTokenRepository.save(token.get());
    }
}
