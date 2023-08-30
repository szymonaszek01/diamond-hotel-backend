package com.app.diamondhotelbackend.service.userprofile;

import com.app.diamondhotelbackend.dto.userprofile.request.UserProfileDetailsUpdateRequestDto;
import com.app.diamondhotelbackend.dto.userprofile.response.UserProfilePictureDetailsResponseDto;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserProfileService {

    UserProfile createUserProfile(UserProfile userProfile);

    UserProfile getUserProfileById(long id);

    UserProfile getUserProfileByEmail(String email);

    List<UserProfile> getUserProfileList();

    UserProfilePictureDetailsResponseDto getUserProfilePictureByEmail(String email) throws UserProfileProcessingException;

    UserProfile updateUserProfile(UserProfile userProfile);

    UserProfile updateUserProfile(String email, UserProfileDetailsUpdateRequestDto userProfileRequestDto) throws UserProfileProcessingException;

    UserProfilePictureDetailsResponseDto updateUserProfilePicture(MultipartFile file, String email) throws UserProfileProcessingException, IOException;

    UserProfile deleteUserProfile(long id) throws UserProfileProcessingException;

    boolean isAdmin(long userProfileId);
}
