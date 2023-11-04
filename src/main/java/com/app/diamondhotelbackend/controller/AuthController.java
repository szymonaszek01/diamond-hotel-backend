package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.auth.request.*;
import com.app.diamondhotelbackend.dto.auth.response.AccountDetailsResponseDto;
import com.app.diamondhotelbackend.entity.ConfirmationToken;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.AuthProcessingException;
import com.app.diamondhotelbackend.exception.ConfirmationTokenProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.service.auth.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RequestMapping("/api/v1/auth")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://diamond-hotel-frontend.vercel.app/"}, allowCredentials = "true")
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/account/login")
    public ResponseEntity<AccountDetailsResponseDto> loginAccount(@RequestBody AccountLoginRequestDto accountLoginRequestDto) {
        try {
            return ResponseEntity.ok(authService.loginAccount(accountLoginRequestDto));
        } catch (AuthProcessingException | UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/account/registration")
    public ResponseEntity<AccountDetailsResponseDto> registerAccount(@RequestBody AccountRegistrationRequestDto accountRegistrationRequestDto) {
        try {
            return ResponseEntity.ok(authService.registerAccount(accountRegistrationRequestDto));
        } catch (AuthProcessingException | UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/confirmation-token/{token}/account/confirmation")
    public ResponseEntity<AccountDetailsResponseDto> confirmAccount(@PathVariable String token) {
        try {
            return ResponseEntity.ok(authService.confirmAccount(token));
        } catch (ConfirmationTokenProcessingException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/email/{email}/account/forgotten/password")
    public ResponseEntity<ConfirmationToken> forgotAccountPassword(@PathVariable String email) {
        try {
            return ResponseEntity.ok(authService.forgotAccountPassword(email));
        } catch (UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/account/email")
    public ResponseEntity<UserProfile> updateAccountEmail(@RequestBody AccountEmailUpdateRequestDto changeEmailRequestDto) {
        try {
            return ResponseEntity.ok(authService.updateAccountEmail(changeEmailRequestDto));
        } catch (AuthProcessingException | ConfirmationTokenProcessingException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/account/forgotten/password")
    public ResponseEntity<UserProfile> updateAccountPassword(@RequestBody AccountForgottenPasswordRequestDto accountForgottenPasswordRequestDto) {
        try {
            return ResponseEntity.ok(authService.updateAccountPassword(accountForgottenPasswordRequestDto));
        } catch (AuthProcessingException | ConfirmationTokenProcessingException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/account/password")
    public ResponseEntity<UserProfile> updateAccountPassword(@RequestBody AccountPasswordUpdateRequestDto accountPasswordUpdateRequestDto) {
        try {
            return ResponseEntity.ok(authService.updateAccountPassword(accountPasswordUpdateRequestDto));
        } catch (AuthProcessingException | ConfirmationTokenProcessingException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/auth-token/{token}")
    public ResponseEntity<AccountDetailsResponseDto> updateAuthToken(@PathVariable String token) {
        try {
            return ResponseEntity.ok(authService.updateAuthToken(token));
        } catch (AuthProcessingException | UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PutMapping("/confirmation-token/{token}")
    public ResponseEntity<ConfirmationToken> updateConfirmationToken(@PathVariable String token) {
        try {
            return ResponseEntity.ok(authService.updateConfirmationToken(token));
        } catch (UserProfileProcessingException | ConfirmationTokenProcessingException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}
