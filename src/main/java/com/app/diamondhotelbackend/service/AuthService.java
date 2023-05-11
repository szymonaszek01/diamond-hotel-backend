package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.LoginRequestDto;
import com.app.diamondhotelbackend.dto.RegisterRequestDto;
import com.app.diamondhotelbackend.dto.UserProfileDetailsResponseDto;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.security.MyUserDetails;
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

    private final JwtService jwtService;

    public Optional<UserProfileDetailsResponseDto> login(LoginRequestDto loginRequestDto) {
        Optional<UserDetails> optionalUserDetails = getUserDetails(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        if (optionalUserDetails.isEmpty()) {
            return Optional.empty();
        }

        String jwt = jwtService.createJwt(optionalUserDetails.get());
        UserProfile userProfile = userProfileService.getUserProfileByEmail(optionalUserDetails.get().getUsername());

        return Optional.of(new UserProfileDetailsResponseDto(jwt, userProfile));
    }

    public Optional<UserProfileDetailsResponseDto> register(RegisterRequestDto registerRequestDto) {
        Optional<UserDetails> optionalUserDetails = getUserDetails(registerRequestDto.getEmail(), registerRequestDto.getPassword());
        if (optionalUserDetails.isPresent()) {
            return Optional.empty();
        }

        UserProfile userProfile = userProfileService.saveUserProfile(registerRequestDto);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(userProfile.getRole()));
        MyUserDetails myUserDetails = new MyUserDetails(userProfile.getId(), userProfile.getEmail(), userProfile.getPassword(), authorities);
        String jwt = jwtService.createJwt(myUserDetails);

        return Optional.of(new UserProfileDetailsResponseDto(jwt, userProfile));
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
