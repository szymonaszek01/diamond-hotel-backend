package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.auth.RegisterRequestDto;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.UserProfileNotFoundException;
import com.app.diamondhotelbackend.repository.UserProfileRepository;
import com.app.diamondhotelbackend.util.Constant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserProfile getUserProfileById(long id) {
        return userProfileRepository.findUserProfileById(id).orElseThrow(() -> new UserProfileNotFoundException(Constant.USER_PROFILE_NOT_FOUND));
    }

    public UserProfile getUserProfileByEmail(String email) {
        return userProfileRepository.findUserProfileByEmail(email).orElseThrow(() -> new UserProfileNotFoundException(Constant.USER_PROFILE_NOT_FOUND));
    }

    public UserProfile saveUserProfile(RegisterRequestDto registerRequestDto) {
        UserProfile userProfile = UserProfile.builder()
                .email(registerRequestDto.getEmail())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .firstname(registerRequestDto.getFirstname())
                .lastname(registerRequestDto.getLastname())
                .age(registerRequestDto.getAge())
                .country(registerRequestDto.getCountry())
                .passportNumber(registerRequestDto.getPassportNumber())
                .phoneNumber(registerRequestDto.getPhoneNumber())
                .city(registerRequestDto.getCity())
                .street(registerRequestDto.getStreet())
                .postalCode(registerRequestDto.getPostalCode())
                .role(Constant.USER)
                .build();

        return userProfileRepository.save(userProfile);
    }
}
