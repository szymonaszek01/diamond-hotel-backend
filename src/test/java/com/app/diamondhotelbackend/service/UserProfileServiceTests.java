package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.userprofile.UpdateUserDetailsRequestDto;
import com.app.diamondhotelbackend.dto.userprofile.UserImageResponseDto;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.repository.UserProfileRepository;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.util.Constant;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserProfileServiceTests {

    @InjectMocks
    private UserProfileServiceImpl userProfileService;

    @Mock
    private UserProfileRepository userProfileRepository;

    private UserProfile userProfile;

    private List<UserProfile> userProfileList;

    private UpdateUserDetailsRequestDto updateUserDetailsRequestDto;

    private MockMultipartFile file;

    @BeforeEach
    public void init() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        userProfile = UserProfile.builder()
                .email("ala-gembala@wp.pl")
                .passportNumber("ZF005401499")
                .role(Constant.USER)
                .authProvider(Constant.LOCAL)
                .accountConfirmed(false)
                .picture(HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d"))
                .build();

        userProfileList = List.of(
                UserProfile.builder()
                        .email("ala-gembala@wp.pl")
                        .password(passwordEncoder.encode("#Test1111"))
                        .passportNumber("DF115499499")
                        .role(Constant.USER)
                        .authProvider(Constant.LOCAL)
                        .accountConfirmed(false)
                        .build(),
                UserProfile.builder()
                        .email("beata-pacanek@wp.pl")
                        .password(passwordEncoder.encode("#Test2222"))
                        .passportNumber("DF115499499")
                        .role(Constant.USER)
                        .authProvider(Constant.LOCAL)
                        .accountConfirmed(false)
                        .build()
        );

        updateUserDetailsRequestDto = UpdateUserDetailsRequestDto.builder()
                .firstname("Ala")
                .lastname("Gembala")
                .age(21)
                .country("Poland")
                .passportNumber("ZF005401400")
                .phoneNumber("999111999")
                .city("Warsaw")
                .street("Adams street")
                .postalCode("09-783")
                .build();

        file = new MockMultipartFile(
                "testFile",
                HexFormat.of().parseHex("a04fd020ea3a6910a2d808002b30309d")
        );
    }

    @Test
    public void UserProfileService_CreateUserProfile_ReturnsUserProfile() {
        when(userProfileRepository.save(Mockito.any(UserProfile.class))).thenReturn(userProfile);

        UserProfile savedUserProfile = userProfileService.createUserProfile(userProfile);

        Assertions.assertThat(savedUserProfile).isNotNull();
        Assertions.assertThat(savedUserProfile.isAccountConfirmed()).isEqualTo(false);
    }


    @Test
    public void UserProfileService_GetUserProfileById_ReturnsUserProfile() {
        when(userProfileRepository.findById(Mockito.any(long.class))).thenReturn(Optional.of(userProfile));

        UserProfile foundUserProfile = userProfileService.getUserProfileById(1);

        Assertions.assertThat(foundUserProfile).isNotNull();
        Assertions.assertThat(foundUserProfile.getPassportNumber()).isEqualTo(userProfile.getPassportNumber());
    }

    @Test
    public void UserProfileService_GetUserProfileByEmail_ReturnsUserProfile() {
        when(userProfileRepository.findByEmail(Mockito.any(String.class))).thenReturn(Optional.of(userProfile));

        UserProfile foundUserProfile = userProfileService.getUserProfileByEmail("ala-gembala@wp.pl");

        Assertions.assertThat(foundUserProfile).isNotNull();
        Assertions.assertThat(foundUserProfile.getPassportNumber()).isEqualTo(userProfile.getPassportNumber());
    }

    @Test
    public void UserProfileService_GetUserProfileList_ReturnsUserProfileList() {
        when(userProfileRepository.findAll()).thenReturn(userProfileList);

        List<UserProfile> foundUserProfileList = userProfileService.getUserProfileList();

        Assertions.assertThat(foundUserProfileList).isNotNull();
        Assertions.assertThat(foundUserProfileList.size()).isEqualTo(2);
    }

    @Test
    public void UserProfileService_GetUserProfileImageByEmail_ReturnsUserImageResponseDto() {
        when(userProfileRepository.findByEmail(Mockito.any(String.class))).thenReturn(Optional.of(userProfile));

        UserImageResponseDto foundUserImageResponseDto = userProfileService.getUserProfilePictureByEmail(userProfile.getEmail());

        Assertions.assertThat(foundUserImageResponseDto).isNotNull();
        Assertions.assertThat(foundUserImageResponseDto.getImage()).isNotNull();
        Assertions.assertThat(foundUserImageResponseDto.getEmail()).isEqualTo(userProfile.getEmail());
    }

    @Test
    public void UserProfileService_UpdateUserProfile_ReturnsUserProfile_AcceptsUserProfile() {
        when(userProfileRepository.save(Mockito.any(UserProfile.class))).thenReturn(userProfile);

        UserProfile updatedUserProfile = userProfileService.updateUserProfile(userProfile);

        Assertions.assertThat(updatedUserProfile).isNotNull();
        Assertions.assertThat(updatedUserProfile.getEmail()).isEqualTo(userProfile.getEmail());
    }

    @Test
    public void UserProfileService_UpdateUserProfile_ReturnsUserProfile_AcceptsUpdateUserDetailsRequestDto() {
        String email = "ala-gembala@wp.pl";

        when(userProfileRepository.findByEmail(Mockito.any(String.class))).thenReturn(Optional.of(userProfile));
        when(userProfileRepository.save(Mockito.any(UserProfile.class))).thenReturn(userProfile);

        UserProfile updatedUserProfile = userProfileService.updateUserProfile(email, updateUserDetailsRequestDto);

        Assertions.assertThat(updatedUserProfile).isNotNull();
        Assertions.assertThat(updatedUserProfile.getEmail()).isEqualTo(email);
        Assertions.assertThat(updatedUserProfile.getFirstname()).isNotNull();
    }

    @Test
    public void UserProfileService_UpdateUserProfilePicture_ReturnsUserImageResponseDto() {
        try {
            String email = "ala-gembala@wp.pl";

            when(userProfileRepository.findByEmail(Mockito.any(String.class))).thenReturn(Optional.of(userProfile));

            UserImageResponseDto updatedUserImageResponseDto = userProfileService.updateUserProfilePicture(file, email);

            Assertions.assertThat(updatedUserImageResponseDto).isNotNull();
            Assertions.assertThat(updatedUserImageResponseDto.getImage()).isNotNull();
            Assertions.assertThat(updatedUserImageResponseDto.getEmail()).isEqualTo(userProfile.getEmail());

        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
    }

    @Test
    public void UserProfileService_DeleteUserProfile_ReturnsUserProfile() {
        when(userProfileRepository.findById(Mockito.any(long.class))).thenReturn(Optional.of(userProfile));

        UserProfile deletedUserProfile = userProfileService.deleteUserProfile(userProfile.getId());

        Assertions.assertThat(deletedUserProfile).isNotNull();
        Assertions.assertThat(deletedUserProfile.getEmail()).isEqualTo(userProfile.getEmail());
    }

    @Test
    public void UserProfileService_IsAdmin_ReturnsBoolean() {
        when(userProfileRepository.findById(Mockito.any(long.class))).thenReturn(Optional.of(userProfile));

        boolean result = userProfileService.isAdmin(1);

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result).isEqualTo(false);
    }
}
