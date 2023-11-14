package com.app.diamondhotelbackend.service.userprofile;

import com.app.diamondhotelbackend.dto.common.FileResponseDto;
import com.app.diamondhotelbackend.dto.userprofile.request.UserProfileDetailsUpdateRequestDto;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.repository.UserProfileRepository;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.UrlUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    public UserProfile createUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    @Override
    public UserProfile getUserProfileById(long id) {
        return userProfileRepository.findById(id).orElseThrow(() -> new UserProfileProcessingException(ConstantUtil.USER_PROFILE_NOT_FOUND_EXCEPTION));
    }

    @Override
    public UserProfile getUserProfileByEmail(String email) {
        return userProfileRepository.findByEmail(email).orElseThrow(() -> new UserProfileProcessingException(ConstantUtil.USER_PROFILE_NOT_FOUND_EXCEPTION));
    }

    @Override
    public List<UserProfile> getUserProfileList() {
        return userProfileRepository.findAll();
    }

    @Override
    public FileResponseDto getUserProfilePictureByEmail(String email) throws UserProfileProcessingException {
        UserProfile userProfile = getUserProfileByEmail(UrlUtil.decode(email));
        String encodedFile = null;
        if (userProfile.getPicture() != null) {
            encodedFile = Base64.getEncoder().encodeToString(userProfile.getPicture());
        }

        return FileResponseDto.builder()
                .fileName(userProfile.getId() + "-image")
                .encodedFile(encodedFile)
                .build();
    }

    @Override
    public UserProfile updateUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    @Override
    public UserProfile updateUserProfile(String email, UserProfileDetailsUpdateRequestDto userProfileRequestDto) throws UserProfileProcessingException {
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

        return userProfileRepository.save(userProfile);
    }

    @Override
    public FileResponseDto updateUserProfilePicture(MultipartFile file, String email) throws UserProfileProcessingException, IOException {
        UserProfile userProfile = getUserProfileByEmail(UrlUtil.decode(email));
        userProfile.setPicture(file.getBytes());
        userProfileRepository.save(userProfile);

        return FileResponseDto.builder().fileName(userProfile.getId() + "-image").encodedFile(Base64.getEncoder().encodeToString(userProfile.getPicture())).build();
    }

    @Override
    public UserProfile deleteUserProfile(long id) throws UserProfileProcessingException {
        UserProfile userProfile = userProfileRepository.findById(id).orElseThrow(() -> new UserProfileProcessingException("User profile not found"));
        userProfileRepository.delete(userProfile);
        return userProfile;
    }

    @Override
    public boolean isAdmin(long id) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(id);
        return userProfile.isPresent() && ConstantUtil.ADMIN.equals(userProfile.get().getRole());
    }
}
