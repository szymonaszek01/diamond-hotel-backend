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

import java.util.List;
import java.util.Optional;

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

    public boolean isAdmin(long userProfileId) {
        Optional<UserProfile> userProfile = userProfileRepository.findUserProfileById(userProfileId);
        return userProfile.isPresent() && Constant.ADMIN.equals(userProfile.get().getRole());
    }

    public List<UserProfile> getUserProfileInfoList() {
        return userProfileRepository.findAll();
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

    public void deleteUserProfile(long id) throws UserProfileNotFoundException {
        UserProfile userProfile = userProfileRepository.findUserProfileById(id).orElseThrow(() -> new UserProfileNotFoundException("User profile not found"));
        userProfileRepository.deleteById(userProfile.getId());
    }
}
