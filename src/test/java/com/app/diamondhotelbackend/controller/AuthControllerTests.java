package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.auth.*;
import com.app.diamondhotelbackend.entity.AuthToken;
import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.security.jwt.JwtFilter;
import com.app.diamondhotelbackend.service.auth.AuthServiceImpl;
import com.app.diamondhotelbackend.util.Constant;
import com.app.diamondhotelbackend.util.UrlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class AuthControllerTests {

    @MockBean
    private AuthServiceImpl authService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private PasswordEncoder passwordEncoder;

    private UserProfile userProfile;

    private AuthToken authToken;

    private ConfirmationToken confirmationToken;

    private UserProfileDetailsResponseDto userProfileDetailsResponseDto;

    private LoginRequestDto loginRequestDto;

    private RegisterRequestDto registerRequestDto;

    private UpdateEmailRequestDto updateEmailRequestDto;

    private UpdatePasswordRequestDto updatePasswordRequestDto;

    private ChangePasswordRequestDto changePasswordRequestDto;

    private static final long CONFIRMATION_TOKEN_EXPIRATION = 1000 * 60 * 15;

    private static final String url = "/api/v1/auth";

    @BeforeEach
    public void init() {
        passwordEncoder = new BCryptPasswordEncoder();

        userProfile = UserProfile.builder()
                .id(1)
                .email("ala-gembala@wp.pl")
                .password(passwordEncoder.encode("#Test1111"))
                .passportNumber("ZF005401499")
                .role(Constant.USER)
                .authProvider(Constant.LOCAL)
                .accountConfirmed(false)
                .build();

        authToken = AuthToken.builder()
                .id(1)
                .userProfile(userProfile)
                .accessValue("accessValue1")
                .refreshValue("refreshValue")
                .build();

        confirmationToken = ConfirmationToken.builder()
                .id(1)
                .userProfile(userProfile)
                .accessValue("accessValue1")
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + CONFIRMATION_TOKEN_EXPIRATION))
                .build();

        userProfileDetailsResponseDto = UserProfileDetailsResponseDto.builder()
                .id(1)
                .email(userProfile.getEmail())
                .accessToken(authToken.getAccessValue())
                .refreshToken(authToken.getRefreshValue())
                .confirmed(true)
                .build();

        loginRequestDto = LoginRequestDto.builder()
                .email(userProfile.getEmail())
                .password(userProfile.getPassword())
                .build();

        registerRequestDto = RegisterRequestDto.builder()
                .email(userProfile.getEmail())
                .password(userProfile.getPassword())
                .passportNumber(userProfile.getPassportNumber())
                .build();

        updateEmailRequestDto = UpdateEmailRequestDto.builder()
                .email(userProfile.getEmail())
                .newEmail("tomek-bomek@gmail.com")
                .build();

        updatePasswordRequestDto = UpdatePasswordRequestDto.builder()
                .email(userProfile.getEmail())
                .newPassword(passwordEncoder.encode("#Test2222"))
                .build();

        changePasswordRequestDto = ChangePasswordRequestDto.builder()
                .token(confirmationToken.getAccessValue())
                .newPassword(passwordEncoder.encode("#Test2222"))
                .build();
    }

    @AfterEach
    public void reinit() {
        userProfile = UserProfile.builder()
                .id(1)
                .email("ala-gembala@wp.pl")
                .password(passwordEncoder.encode("#Test1111"))
                .passportNumber("ZF005401499")
                .role(Constant.USER)
                .authProvider(Constant.LOCAL)
                .accountConfirmed(false)
                .build();

        authToken = AuthToken.builder()
                .id(1)
                .userProfile(userProfile)
                .accessValue("accessValue1")
                .refreshValue("refreshValue")
                .build();

        confirmationToken = ConfirmationToken.builder()
                .id(1)
                .userProfile(userProfile)
                .accessValue("accessValue1")
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + CONFIRMATION_TOKEN_EXPIRATION))
                .build();
    }

    @Test
    public void AuthController_LoginAccount_ReturnsUserProfileDetailsResponseDto() throws Exception {
        userProfile.setAccountConfirmed(true);

        when(authService.loginAccount(Mockito.any(LoginRequestDto.class))).thenReturn(userProfileDetailsResponseDto);

        MockHttpServletRequestBuilder request = post(url + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) userProfileDetailsResponseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(userProfileDetailsResponseDto.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token", CoreMatchers.is(userProfileDetailsResponseDto.getAccessToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refresh_token", CoreMatchers.is(userProfileDetailsResponseDto.getRefreshToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.confirmed", CoreMatchers.is(userProfileDetailsResponseDto.isConfirmed())));
    }

    @Test
    public void AuthController_RegisterAccount_ReturnsUserProfileDetailsResponseDto() throws Exception {
        userProfileDetailsResponseDto.setConfirmed(false);

        when(authService.registerAccount(Mockito.any(RegisterRequestDto.class))).thenReturn(userProfileDetailsResponseDto);

        MockHttpServletRequestBuilder request = post(url + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequestDto));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) userProfileDetailsResponseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(userProfileDetailsResponseDto.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token", CoreMatchers.is(userProfileDetailsResponseDto.getAccessToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refresh_token", CoreMatchers.is(userProfileDetailsResponseDto.getRefreshToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.confirmed", CoreMatchers.is(userProfileDetailsResponseDto.isConfirmed())));
    }

    @Test
    public void AuthController_ConfirmAccount_ReturnsUserProfileDetailsResponseDto() throws Exception {
        when(authService.confirmAccount(Mockito.any(String.class))).thenReturn(userProfileDetailsResponseDto);

        MockHttpServletRequestBuilder request = get(url + "/confirm/account/confirmation-token/" + UrlUtil.encode(confirmationToken.getAccessValue()))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) userProfileDetailsResponseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(userProfileDetailsResponseDto.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token", CoreMatchers.is(userProfileDetailsResponseDto.getAccessToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refresh_token", CoreMatchers.is(userProfileDetailsResponseDto.getRefreshToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.confirmed", CoreMatchers.is(userProfileDetailsResponseDto.isConfirmed())));
    }

    @Test
    public void AuthController_ForgotAccountPassword_ReturnsConfirmationToken() throws Exception {
        when(authService.forgotAccountPassword(Mockito.any(String.class))).thenReturn(confirmationToken);

        MockHttpServletRequestBuilder request = get(url + "/forgot/password/email/" + UrlUtil.encode(userProfile.getEmail()))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) confirmationToken.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_value", CoreMatchers.is(confirmationToken.getAccessValue())));
    }

    @Test
    public void AuthController_UpdateAccountEmail_ReturnsUserProfile() throws Exception {
        userProfile.setAccountConfirmed(true);
        userProfile.setEmail(updateEmailRequestDto.getNewEmail());

        when(authService.updateAccountEmail(Mockito.any(UpdateEmailRequestDto.class))).thenReturn(userProfile);

        MockHttpServletRequestBuilder request = put(url + "/update/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateEmailRequestDto));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) userProfile.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(userProfile.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.confirmed", CoreMatchers.is(userProfile.isAccountConfirmed())));
    }

    @Test
    public void AuthController_UpdateAccountPassword_ReturnsUserProfileDetailsResponseDto_AcceptsChangePasswordRequestDto() throws Exception {
        userProfile.setPassword(changePasswordRequestDto.getNewPassword());

        when(authService.updateAccountPassword(Mockito.any(ChangePasswordRequestDto.class))).thenReturn(userProfile);

        MockHttpServletRequestBuilder request = put(url + "/forgot/password/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequestDto));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) userProfile.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.confirmed", CoreMatchers.is(userProfile.isAccountConfirmed())));
    }

    @Test
    public void AuthController_UpdateAccountPassword_ReturnsUserProfileDetailsResponseDto_AcceptsUpdatePasswordRequestDto() throws Exception {
        userProfile.setAccountConfirmed(true);
        userProfile.setPassword(updatePasswordRequestDto.getNewPassword());

        when(authService.updateAccountPassword(Mockito.any(UpdatePasswordRequestDto.class))).thenReturn(userProfile);

        MockHttpServletRequestBuilder request = put(url + "/update/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePasswordRequestDto));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) userProfile.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.confirmed", CoreMatchers.is(userProfile.isAccountConfirmed())));
    }

    @Test
    public void AuthController_UpdateAuthToken_ReturnsUserProfileDetailsResponseDto() throws Exception {
        userProfile.setAccountConfirmed(true);
        userProfileDetailsResponseDto.setAccessToken("accessValue2");

        when(authService.updateAuthToken(Mockito.any(String.class))).thenReturn(userProfileDetailsResponseDto);

        MockHttpServletRequestBuilder request = get(url + "/refresh/access-token/" + UrlUtil.encode(authToken.getAccessValue()))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) userProfileDetailsResponseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(userProfileDetailsResponseDto.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token", CoreMatchers.is(userProfileDetailsResponseDto.getAccessToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refresh_token", CoreMatchers.is(userProfileDetailsResponseDto.getRefreshToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.confirmed", CoreMatchers.is(userProfileDetailsResponseDto.isConfirmed())));
    }

    @Test
    public void AuthController_UpdateConfirmationToken_ReturnsConfirmationToken() throws Exception {
        String token = confirmationToken.getAccessValue();
        confirmationToken.setAccessValue("accessValue2");

        when(authService.updateConfirmationToken(Mockito.any(String.class))).thenReturn(confirmationToken);

        MockHttpServletRequestBuilder request = get(url + "/refresh/confirmation-token/" + UrlUtil.encode(token))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) confirmationToken.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_value", CoreMatchers.is(confirmationToken.getAccessValue())));
    }
}
