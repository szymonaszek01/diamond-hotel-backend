package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.auth.request.*;
import com.app.diamondhotelbackend.dto.auth.response.AccountDetailsResponseDto;
import com.app.diamondhotelbackend.entity.AuthToken;
import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.security.jwt.JwtFilter;
import com.app.diamondhotelbackend.service.auth.AuthServiceImpl;
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

    private UserProfile userProfile;

    private AuthToken authToken;

    private ConfirmationToken confirmationToken;

    private AccountDetailsResponseDto accountDetailsResponseDto;

    private AccountLoginRequestDto accountLoginRequestDto;

    private AccountRegistrationRequestDto accountRegistrationRequestDto;

    private AccountEmailUpdateRequestDto accountEmailUpdateRequestDto;

    private AccountPasswordUpdateRequestDto accountPasswordUpdateRequestDto;

    private AccountForgottenPasswordRequestDto accountForgottenPasswordRequestDto;

    private static final long CONFIRMATION_TOKEN_EXPIRATION = 1000 * 60 * 15;

    private static final String url = "/api/v1/auth";

    @BeforeEach
    public void init() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        userProfile = UserProfile.builder()
                .id(1)
                .email("ala-gembala@wp.pl")
                .password(passwordEncoder.encode("#Test1111"))
                .passportNumber("ZF005401499")
                .role(ConstantUtil.USER)
                .authProvider(ConstantUtil.LOCAL)
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

        accountDetailsResponseDto = AccountDetailsResponseDto.builder()
                .id(1)
                .email(userProfile.getEmail())
                .accessToken(authToken.getAccessValue())
                .refreshToken(authToken.getRefreshValue())
                .confirmed(true)
                .build();

        accountLoginRequestDto = AccountLoginRequestDto.builder()
                .email(userProfile.getEmail())
                .password(userProfile.getPassword())
                .build();

        accountRegistrationRequestDto = AccountRegistrationRequestDto.builder()
                .email(userProfile.getEmail())
                .password(userProfile.getPassword())
                .passportNumber(userProfile.getPassportNumber())
                .build();

        accountEmailUpdateRequestDto = AccountEmailUpdateRequestDto.builder()
                .email(userProfile.getEmail())
                .newEmail("tomek-bomek@gmail.com")
                .build();

        accountPasswordUpdateRequestDto = AccountPasswordUpdateRequestDto.builder()
                .email(userProfile.getEmail())
                .newPassword(passwordEncoder.encode("#Test2222"))
                .build();

        accountForgottenPasswordRequestDto = AccountForgottenPasswordRequestDto.builder()
                .token(confirmationToken.getAccessValue())
                .newPassword(passwordEncoder.encode("#Test2222"))
                .build();
    }

    @Test
    public void AuthController_LoginAccount_ReturnsAccountDetailsResponseDto() throws Exception {
        userProfile.setAccountConfirmed(true);

        when(authService.loginAccount(Mockito.any(AccountLoginRequestDto.class))).thenReturn(accountDetailsResponseDto);

        MockHttpServletRequestBuilder request = post(url + "/account/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountLoginRequestDto));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) accountDetailsResponseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(accountDetailsResponseDto.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token", CoreMatchers.is(accountDetailsResponseDto.getAccessToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refresh_token", CoreMatchers.is(accountDetailsResponseDto.getRefreshToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.confirmed", CoreMatchers.is(accountDetailsResponseDto.isConfirmed())));
    }

    @Test
    public void AuthController_RegisterAccount_ReturnsAccountDetailsResponseDto() throws Exception {
        accountDetailsResponseDto.setConfirmed(false);

        when(authService.registerAccount(Mockito.any(AccountRegistrationRequestDto.class))).thenReturn(accountDetailsResponseDto);

        MockHttpServletRequestBuilder request = post(url + "/account/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRegistrationRequestDto));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) accountDetailsResponseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(accountDetailsResponseDto.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token", CoreMatchers.is(accountDetailsResponseDto.getAccessToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refresh_token", CoreMatchers.is(accountDetailsResponseDto.getRefreshToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.confirmed", CoreMatchers.is(accountDetailsResponseDto.isConfirmed())));
    }

    @Test
    public void AuthController_ConfirmAccount_ReturnsAccountDetailsResponseDto() throws Exception {
        when(authService.confirmAccount(Mockito.any(String.class))).thenReturn(accountDetailsResponseDto);

        MockHttpServletRequestBuilder request = put(url + "/confirmation-token/" + UrlUtil.encode(confirmationToken.getAccessValue()) + "/account/confirmation")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) accountDetailsResponseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(accountDetailsResponseDto.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token", CoreMatchers.is(accountDetailsResponseDto.getAccessToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refresh_token", CoreMatchers.is(accountDetailsResponseDto.getRefreshToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.confirmed", CoreMatchers.is(accountDetailsResponseDto.isConfirmed())));
    }

    @Test
    public void AuthController_ForgotAccountPassword_ReturnsConfirmationToken() throws Exception {
        when(authService.forgotAccountPassword(Mockito.any(String.class))).thenReturn(confirmationToken);

        MockHttpServletRequestBuilder request = get(url + "/email/" + UrlUtil.encode(userProfile.getEmail()) + "/account/forgotten/password")
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
        userProfile.setEmail(accountEmailUpdateRequestDto.getNewEmail());

        when(authService.updateAccountEmail(Mockito.any(AccountEmailUpdateRequestDto.class))).thenReturn(userProfile);

        MockHttpServletRequestBuilder request = put(url + "/account/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountEmailUpdateRequestDto));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) userProfile.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(userProfile.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.confirmed", CoreMatchers.is(userProfile.isAccountConfirmed())));
    }

    @Test
    public void AuthController_UpdateAccountPassword_ReturnsAccountDetailsResponseDto_AcceptsForgotAccountPasswordRequestDto() throws Exception {
        userProfile.setPassword(accountForgottenPasswordRequestDto.getNewPassword());

        when(authService.updateAccountPassword(Mockito.any(AccountForgottenPasswordRequestDto.class))).thenReturn(userProfile);

        MockHttpServletRequestBuilder request = put(url + "/account/forgotten/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountForgottenPasswordRequestDto));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) userProfile.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.confirmed", CoreMatchers.is(userProfile.isAccountConfirmed())));
    }

    @Test
    public void AuthController_UpdateAccountPassword_ReturnsAccountDetailsResponseDto_AcceptsUpdateAccountPasswordRequestDto() throws Exception {
        userProfile.setAccountConfirmed(true);
        userProfile.setPassword(accountPasswordUpdateRequestDto.getNewPassword());

        when(authService.updateAccountPassword(Mockito.any(AccountPasswordUpdateRequestDto.class))).thenReturn(userProfile);

        MockHttpServletRequestBuilder request = put(url + "/account/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountPasswordUpdateRequestDto));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) userProfile.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.confirmed", CoreMatchers.is(userProfile.isAccountConfirmed())));
    }

    @Test
    public void AuthController_UpdateAuthToken_ReturnsAccountDetailsResponseDto() throws Exception {
        userProfile.setAccountConfirmed(true);
        accountDetailsResponseDto.setAccessToken("accessValue2");

        when(authService.updateAuthToken(Mockito.any(String.class))).thenReturn(accountDetailsResponseDto);

        MockHttpServletRequestBuilder request = put(url + "/auth-token/" + UrlUtil.encode(authToken.getAccessValue()))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) accountDetailsResponseDto.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email", CoreMatchers.is(accountDetailsResponseDto.getEmail())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token", CoreMatchers.is(accountDetailsResponseDto.getAccessToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.refresh_token", CoreMatchers.is(accountDetailsResponseDto.getRefreshToken())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.confirmed", CoreMatchers.is(accountDetailsResponseDto.isConfirmed())));
    }

    @Test
    public void AuthController_UpdateConfirmationToken_ReturnsConfirmationToken() throws Exception {
        String token = confirmationToken.getAccessValue();
        confirmationToken.setAccessValue("accessValue2");

        when(authService.updateConfirmationToken(Mockito.any(String.class))).thenReturn(confirmationToken);

        MockHttpServletRequestBuilder request = put(url + "/confirmation-token/" + UrlUtil.encode(token))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) confirmationToken.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_value", CoreMatchers.is(confirmationToken.getAccessValue())));
    }
}
