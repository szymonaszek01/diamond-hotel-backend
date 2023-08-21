package com.app.diamondhotelbackend.service.userprofile;

import com.app.diamondhotelbackend.dto.auth.RegisterRequestDto;
import com.app.diamondhotelbackend.dto.userprofile.UpdateUserDetailsRequestDto;
import com.app.diamondhotelbackend.dto.userprofile.UserImageResponseDto;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.repository.UserProfileRepository;
import com.app.diamondhotelbackend.service.userprofile.UserProfileService;
import com.app.diamondhotelbackend.util.Constant;
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
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserProfile getUserProfileById(long id) {
        return userProfileRepository.findUserProfileById(id).orElseThrow(() -> new UserProfileProcessingException(Constant.USER_PROFILE_NOT_FOUND_EXCEPTION));
    }

    @Override
    public UserProfile getUserProfileByEmail(String email) {
        return userProfileRepository.findUserProfileByEmail(email).orElseThrow(() -> new UserProfileProcessingException(Constant.USER_PROFILE_NOT_FOUND_EXCEPTION));
    }

    @Override
    public boolean isAdmin(long userProfileId) {
        Optional<UserProfile> userProfile = userProfileRepository.findUserProfileById(userProfileId);
        return userProfile.isPresent() && Constant.ADMIN.equals(userProfile.get().getRole());
    }

    @Override
    public List<UserProfile> getUserProfileInfoList() {
        return userProfileRepository.findAll();
    }

    @Override
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

    @Override
    public void saveUserProfile(UserProfile userProfile) {
        userProfileRepository.save(userProfile);
    }

    @Override
    public void updateUserProfile(String email, UpdateUserDetailsRequestDto userProfileRequestDto) throws UserProfileProcessingException {
        UserProfile userProfile = getUserProfileByEmail(email);

        userProfile.setFirstname(userProfileRequestDto.getFirstname());
        userProfile.setLastname(userProfileRequestDto.getLastname());
        userProfile.setAge(userProfileRequestDto.getAge());
        userProfile.setCountry(userProfileRequestDto.getCountry());
        userProfile.setPassportNumber(userProfileRequestDto.getPassportNumber());
        userProfile.setPhoneNumber(userProfileRequestDto.getPhoneNumber());
        userProfile.setCity(userProfileRequestDto.getCity());
        userProfile.setStreet(userProfileRequestDto.getStreet());
        userProfile.setPostalCode(userProfileRequestDto.getPostalCode());

        userProfileRepository.save(userProfile);
    }

    @Override
    public void deleteUserProfile(long id) throws UserProfileProcessingException {
        UserProfile userProfile = userProfileRepository.findUserProfileById(id).orElseThrow(() -> new UserProfileProcessingException("User profile not found"));
        userProfileRepository.deleteById(userProfile.getId());
    }

    @Override
    public boolean isNewPasswordUnique(String password) {
        return userProfileRepository.findAll()
                .stream()
                .filter(userProfile -> !Constant.OAUTH2.equals(userProfile.getAuthProvider()))
                .noneMatch(userProfile -> passwordEncoder.matches(password, userProfile.getPassword()));
    }

    @Override
    public boolean isNewEmailUnique(String email) {
        return userProfileRepository.findAll()
                .stream()
                .filter(userProfile -> !Constant.OAUTH2.equals(userProfile.getAuthProvider()))
                .noneMatch(userProfile -> userProfile.getEmail().equals(email));
    }

    @Override
    public UserImageResponseDto updateUserImage(MultipartFile file, String email) throws UserProfileProcessingException, IOException {
        UserProfile userProfile = getUserProfileByEmail(UrlUtil.decode(email));
        userProfile.setPicture(file.getBytes());
        userProfileRepository.save(userProfile);

        return UserImageResponseDto.builder().email(userProfile.getEmail()).image(file.getBytes()).build();
    }

    @Override
    public UserImageResponseDto getUserImageByEmail(String email) throws UserProfileProcessingException {
        UserProfile userProfile = getUserProfileByEmail(UrlUtil.decode(email));
        return UserImageResponseDto.builder().email(userProfile.getEmail()).image(userProfile.getPicture()).build();
    }
}
