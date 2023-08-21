package com.app.diamondhotelbackend.service.userprofile;

import com.app.diamondhotelbackend.dto.auth.RegisterRequestDto;
import com.app.diamondhotelbackend.dto.userprofile.UpdateUserDetailsRequestDto;
import com.app.diamondhotelbackend.dto.userprofile.UserImageResponseDto;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserProfileService {

    UserProfile getUserProfileById(long id);

    UserProfile getUserProfileByEmail(String email);

    boolean isAdmin(long userProfileId);

    List<UserProfile> getUserProfileInfoList();

    UserProfile saveUserProfile(RegisterRequestDto registerRequestDto);

    void saveUserProfile(UserProfile userProfile);

    void updateUserProfile(String email, UpdateUserDetailsRequestDto userProfileRequestDto) throws UserProfileProcessingException;

    void deleteUserProfile(long id) throws UserProfileProcessingException;

    boolean isNewPasswordUnique(String password);

    boolean isNewEmailUnique(String email);

    UserImageResponseDto updateUserImage(MultipartFile file, String email) throws UserProfileProcessingException, IOException;

    UserImageResponseDto getUserImageByEmail(String email) throws UserProfileProcessingException;
}
