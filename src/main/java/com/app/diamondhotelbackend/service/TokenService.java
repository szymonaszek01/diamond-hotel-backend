package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.entity.Token;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.repository.TokenRepository;
import com.app.diamondhotelbackend.security.jwt.JwtProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class TokenService {

    private final UserProfileService userProfileService;

    private final TokenRepository tokenRepository;

    private final JwtProvider jwtProvider;

    public Optional<Token> refreshAccessToken(String refreshToken) {
        Optional<Token> token = tokenRepository.findTokenByRefreshValue(refreshToken);
        if (token.isEmpty()) {
            return Optional.empty();
        }

        Optional<UserDetails> userDetails = jwtProvider.validateToken(refreshToken);
        if (userDetails.isEmpty()) {
            tokenRepository.delete(token.get());
            return Optional.empty();
        }

        token.get().setAccessValue(jwtProvider.createToken(userDetails.get(), jwtProvider.getAccessTokenExpiration()));
        return Optional.of(tokenRepository.save(token.get()));
    }

    public Token saveToken(UserDetails userDetails) throws UserProfileProcessingException {
        UserProfile userProfile = userProfileService.getUserProfileByEmail(userDetails.getUsername());
        Optional<Token> token = tokenRepository.findTokenByUserProfile(userProfile);
        if (token.isPresent()) {
            token.get().setAccessValue(jwtProvider.createToken(userDetails, jwtProvider.getAccessTokenExpiration()));
            token.get().setRefreshValue(jwtProvider.createToken(userDetails, jwtProvider.getRefreshTokenExpiration()));
        } else {
            token = Optional.of(Token.builder()
                    .userProfile(userProfile)
                    .accessValue(jwtProvider.createToken(userDetails, jwtProvider.getAccessTokenExpiration()))
                    .refreshValue(jwtProvider.createToken(userDetails, jwtProvider.getRefreshTokenExpiration()))
                    .build());
        }

        return tokenRepository.save(token.get());
    }
}
