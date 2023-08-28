package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.auth.*;
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
@CrossOrigin(origins = {"https://diamond-hotel-frontend.vercel.app", "http://localhost:4200", "http://localhost:3000"}, allowCredentials = "true")
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    public ResponseEntity<UserProfileDetailsResponseDto> loginAccount(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            return ResponseEntity.ok(authService.loginAccount(loginRequestDto));
        } catch (AuthProcessingException | UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserProfileDetailsResponseDto> registerAccount(@RequestBody RegisterRequestDto registerRequestDto) {
        try {
            return ResponseEntity.ok(authService.registerAccount(registerRequestDto));
        } catch (AuthProcessingException | UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/confirm/account/confirmation-token/{token}")
    public ResponseEntity<UserProfileDetailsResponseDto> confirmAccount(@PathVariable String token) {
        try {
            return ResponseEntity.ok(authService.confirmAccount(token));
        } catch (ConfirmationTokenProcessingException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/forgot/password/email/{email}")
    public ResponseEntity<ConfirmationToken> forgotAccountPassword(@PathVariable String email) {
        try {
            return ResponseEntity.ok(authService.forgotAccountPassword(email));
        } catch (UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/update/email")
    public ResponseEntity<UserProfile> updateAccountEmail(@RequestBody UpdateEmailRequestDto changeEmailRequestDto) {
        try {
            return ResponseEntity.ok(authService.updateAccountEmail(changeEmailRequestDto));
        } catch (AuthProcessingException | ConfirmationTokenProcessingException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/forgot/password/new")
    public ResponseEntity<UserProfile> updateAccountPassword(@RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        try {
            return ResponseEntity.ok(authService.updateAccountPassword(changePasswordRequestDto));
        } catch (AuthProcessingException | ConfirmationTokenProcessingException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @PutMapping("/update/password")
    public ResponseEntity<UserProfile> updateAccountPassword(@RequestBody UpdatePasswordRequestDto updatePasswordRequestDto) {
        try {
            return ResponseEntity.ok(authService.updateAccountPassword(updatePasswordRequestDto));
        } catch (AuthProcessingException | ConfirmationTokenProcessingException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/refresh/access-token/{token}")
    public ResponseEntity<UserProfileDetailsResponseDto> updateAuthToken(@PathVariable String token) {
        try {
            return ResponseEntity.ok(authService.updateAuthToken(token));
        } catch (AuthProcessingException | UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/refresh/confirmation-token/{token}")
    public ResponseEntity<ConfirmationToken> updateConfirmationToken(@PathVariable String token) {
        try {
            return ResponseEntity.ok(authService.updateConfirmationToken(token));
        } catch (UserProfileProcessingException | ConfirmationTokenProcessingException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}
