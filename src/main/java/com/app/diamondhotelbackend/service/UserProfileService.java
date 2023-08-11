package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.auth.RegisterRequestDto;
import com.app.diamondhotelbackend.dto.userprofile.UserImageResponseDto;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.repository.UserProfileRepository;
import com.app.diamondhotelbackend.util.Constant;
import com.app.diamondhotelbackend.util.ImageUtil;
import com.app.diamondhotelbackend.util.UrlUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final ImageUtil imageUtil;

    private final UrlUtil urlUtil;

    public UserProfile getUserProfileById(long id) {
        return userProfileRepository.findUserProfileById(id).orElseThrow(() -> new UserProfileProcessingException(Constant.USER_PROFILE_NOT_FOUND_EXCEPTION));
    }

    public UserProfile getUserProfileByEmail(String email) {
        UserProfile userProfile = userProfileRepository.findUserProfileByEmail(email).orElseThrow(() -> new UserProfileProcessingException(Constant.USER_PROFILE_NOT_FOUND_EXCEPTION));
        byte[] picture = userProfile.getPicture();

        return UserProfile.builder()
                .id(userProfile.getId())
                .email(userProfile.getEmail())
                .password(userProfile.getPassword())
                .firstname(userProfile.getFirstname())
                .lastname(userProfile.getLastname())
                .age(userProfile.getAge())
                .country(userProfile.getCountry())
                .passportNumber(userProfile.getPassportNumber())
                .phoneNumber(userProfile.getPhoneNumber())
                .city(userProfile.getCity())
                .street(userProfile.getStreet())
                .postalCode(userProfile.getPostalCode())
                .authProvider(userProfile.getAuthProvider())
                .picture(imageUtil.decompressImage(picture))
                .role(userProfile.getRole())
                .accountConfirmed(userProfile.isAccountConfirmed())
                .build();
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
                .authProvider(Constant.LOCAL)
                .accountConfirmed(false)
                .build();

        return userProfileRepository.save(userProfile);
    }

    public void saveUserProfile(UserProfile userProfile) {
        userProfileRepository.save(userProfile);
    }

    public void deleteUserProfile(long id) throws UserProfileProcessingException {
        UserProfile userProfile = userProfileRepository.findUserProfileById(id).orElseThrow(() -> new UserProfileProcessingException("User profile not found"));
        userProfileRepository.deleteById(userProfile.getId());
    }

    public boolean isNewPasswordUnique(String password) {
        return userProfileRepository.findAll()
                .stream()
                .filter(userProfile -> !Constant.OAUTH2.equals(userProfile.getAuthProvider()))
                .noneMatch(userProfile -> passwordEncoder.matches(password, userProfile.getPassword()));
    }

    public UserImageResponseDto updateUserImageByEmail(MultipartFile file, String email) throws UserProfileProcessingException, IOException {
        UserProfile userProfile = getUserProfileByEmail(urlUtil.decode(email));
        userProfile.setPicture(imageUtil.compressImage(file.getBytes()));
        userProfileRepository.save(userProfile);

        return UserImageResponseDto.builder().email(userProfile.getEmail()).image(file.getBytes()).build();
    }

    public UserImageResponseDto getUserImageByEmail(String email) throws UserProfileProcessingException {
        UserProfile userProfile = getUserProfileByEmail(urlUtil.decode(email));
        byte[] image = imageUtil.decompressImage(userProfile.getPicture());

        return UserImageResponseDto.builder().email(userProfile.getEmail()).image(image).build();
    }
}
