package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.common.FileResponseDto;
import com.app.diamondhotelbackend.dto.userprofile.request.UserProfileDetailsUpdateRequestDto;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.security.jwt.JwtFilter;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.UrlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = UserProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserProfileControllerTests {

    private static final String url = "/api/v1/user-profile";
    @MockBean
    private UserProfileServiceImpl userProfileService;
    @MockBean
    private JwtFilter jwtFilter;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private UserProfile userProfile;
    private UserProfile updatedUserProfile;
    private List<UserProfile> userProfileList;
    private UserProfileDetailsUpdateRequestDto userProfileDetailsUpdateRequestDto;
    private FileResponseDto fileResponseDto;
    private MockMultipartFile file;

    @BeforeEach
    public void init() {
        userProfile = UserProfile.builder()
                .id(1)
                .email("ala-gembala@wp.pl")
                .passportNumber("ZF005401499")
                .role(ConstantUtil.USER)
                .authProvider(ConstantUtil.LOCAL)
                .accountConfirmed(false)
                .picture(HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d"))
                .build();

        updatedUserProfile = UserProfile.builder()
                .id(1)
                .email("ala-gembala@wp.pl")
                .role(ConstantUtil.USER)
                .authProvider(ConstantUtil.LOCAL)
                .accountConfirmed(false)
                .picture(HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d"))
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

        userProfileList = List.of(
                UserProfile.builder()
                        .id(1)
                        .email("ala-gembala@wp.pl")
                        .passportNumber("DF115499499")
                        .role(ConstantUtil.USER)
                        .authProvider(ConstantUtil.LOCAL)
                        .accountConfirmed(false)
                        .build(),
                UserProfile.builder()
                        .id(2)
                        .email("beata-pacanek@wp.pl")
                        .passportNumber("DF115499499")
                        .role(ConstantUtil.USER)
                        .authProvider(ConstantUtil.LOCAL)
                        .accountConfirmed(false)
                        .build()
        );

        userProfileDetailsUpdateRequestDto = UserProfileDetailsUpdateRequestDto.builder()
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

        fileResponseDto = FileResponseDto.builder()
                .fileName(userProfile.getId() + "-image")
                .encodedFile(Base64.getEncoder().encodeToString(userProfile.getPicture()))
                .build();

        file = new MockMultipartFile(
                "image",
                "test.txt",
                "text/plain",
                HexFormat.of().parseHex("a04fd020ea3a6910a2d808002b30309d")
        );
    }

    @Test
    public void UserProfileController_GetUserProfileById_ReturnsUserProfile() throws Exception {
        when(userProfileService.getUserProfileById(Mockito.any(long.class))).thenReturn(userProfile);

        MockHttpServletRequestBuilder request = get(url + "/id/" + userProfile.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(userProfile.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.passport_number", CoreMatchers.is(userProfile.getPassportNumber())));
    }

    @Test
    public void UserProfileController_GetUserProfileByEmail_ReturnsUserProfile() throws Exception {
        when(userProfileService.getUserProfileByEmail(Mockito.any(String.class))).thenReturn(userProfile);

        MockHttpServletRequestBuilder request = get(url + "/email/" + UrlUtil.encode(userProfile.getEmail()))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(userProfile.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.passport_number", CoreMatchers.is(userProfile.getPassportNumber())));
    }

    @Test
    public void UserProfileController_GetUserProfileList_ReturnsUserProfileList() throws Exception {
        when(userProfileService.getUserProfileList()).thenReturn(userProfileList);

        MockHttpServletRequestBuilder request = get(url + "/all")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void UserProfileController_GetUserProfilePictureByEmail_ReturnsFileResponseDto() throws Exception {
        when(userProfileService.getUserProfilePictureByEmail(Mockito.any(String.class))).thenReturn(fileResponseDto);

        MockHttpServletRequestBuilder request = get(url + "/email/" + UrlUtil.encode(userProfile.getEmail()) + "/picture")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.file_name", CoreMatchers.is(userProfile.getId() + "-image")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.encoded_file", CoreMatchers.is(fileResponseDto.getEncodedFile())));
    }

    @Test
    public void UserProfileController_UpdateUserProfile_ReturnsUserProfile() throws Exception {
        when(userProfileService.updateUserProfile(Mockito.any(String.class), Mockito.any(UserProfileDetailsUpdateRequestDto.class))).thenReturn(updatedUserProfile);

        MockHttpServletRequestBuilder request = put(url + "/email/" + UrlUtil.encode(userProfile.getEmail()) + "/details")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userProfileDetailsUpdateRequestDto));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(userProfile.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstname", CoreMatchers.is(userProfileDetailsUpdateRequestDto.getFirstname())));
    }

    @Test
    public void UserProfileController_UpdateUserProfilePicture_ReturnsFileResponseDto() throws Exception {
        when(userProfileService.updateUserProfilePicture(Mockito.any(MultipartFile.class), Mockito.any(String.class))).thenReturn(fileResponseDto);

        MockHttpServletRequestBuilder request = multipart(url + "/email/" + UrlUtil.encode(userProfile.getEmail()) + "/picture")
                .file(file);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.file_name", CoreMatchers.is(userProfile.getId() + "-image")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.encoded_file", CoreMatchers.is(fileResponseDto.getEncodedFile())));
    }

    @Test
    public void UserProfileController_DeleteUserProfile_ReturnsUserProfile() throws Exception {
        when(userProfileService.deleteUserProfile(Mockito.any(long.class))).thenReturn(userProfile);

        MockHttpServletRequestBuilder request = delete(url + "/id/" + userProfile.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(userProfile.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.passport_number", CoreMatchers.is(userProfile.getPassportNumber())));
    }
}
