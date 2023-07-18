package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.auth.LoginRequestDto;
import com.app.diamondhotelbackend.dto.auth.RegisterRequestDto;
import com.app.diamondhotelbackend.dto.auth.TokenRefreshRequestDto;
import com.app.diamondhotelbackend.dto.auth.UserProfileDetailsResponseDto;
import com.app.diamondhotelbackend.entity.Token;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.AuthProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.security.jwt.CustomUserDetails;
import com.app.diamondhotelbackend.util.Constant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final UserProfileService userProfileService;

    private final TokenService tokenService;

    public UserProfileDetailsResponseDto login(LoginRequestDto loginRequestDto) throws AuthProcessingException, UserProfileProcessingException {
        Optional<UserDetails> optionalUserDetails = getUserDetails(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        if (optionalUserDetails.isEmpty()) {
            throw new AuthProcessingException(Constant.USER_PROFILE_NOT_FOUND_EXCEPTION);
        }

        return createUserProfileDetailsResponseDto(optionalUserDetails.get());
    }

    public UserProfileDetailsResponseDto register(RegisterRequestDto registerRequestDto) throws AuthProcessingException, UserProfileProcessingException {
        Optional<UserDetails> optionalUserDetails = getUserDetails(registerRequestDto.getEmail(), registerRequestDto.getPassword());
        if (optionalUserDetails.isPresent()) {
            throw new AuthProcessingException(Constant.USER_PROFILE_EXISTS_EXCEPTION);
        }

        UserProfile userProfile = userProfileService.saveUserProfile(registerRequestDto);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(userProfile.getRole()));
        return createUserProfileDetailsResponseDto(new CustomUserDetails(userProfile.getId(), userProfile.getEmail(), userProfile.getPassword(), authorities));
    }

    public UserProfileDetailsResponseDto refreshAccessToken(TokenRefreshRequestDto tokenRefreshRequestDto) throws AuthProcessingException {
        return tokenService.refreshAccessToken(tokenRefreshRequestDto.getRefreshToken())
                .map(token -> UserProfileDetailsResponseDto.builder()
                        .accessToken(token.getAccessValue())
                        .refreshToken(token.getRefreshValue())
                        .email(token.getUserProfile().getEmail())
                        .build()
                )
                .orElseThrow(() -> new AuthProcessingException(Constant.INVALID_TOKEN_EXCEPTION));
    }

    public UserProfileDetailsResponseDto createUserProfileDetailsResponseDto(UserDetails userDetails) throws UserProfileProcessingException {
        Token token = tokenService.saveToken(userDetails);
        return UserProfileDetailsResponseDto.builder()
                .accessToken(token.getAccessValue())
                .refreshToken(token.getRefreshValue())
                .email(token.getUserProfile().getEmail())
                .build();
    }

    private Optional<UserDetails> getUserDetails(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            return Optional.of(userDetails);

        } catch (Exception e) {
            log.error(e.getMessage());

            return Optional.empty();
        }
    }
}
