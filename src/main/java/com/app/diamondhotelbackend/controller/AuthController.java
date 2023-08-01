package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.auth.*;
import com.app.diamondhotelbackend.exception.AuthProcessingException;
import com.app.diamondhotelbackend.exception.ConfirmationTokenProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.service.AuthService;
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

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserProfileDetailsResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            return ResponseEntity.ok(authService.login(loginRequestDto));
        } catch (AuthProcessingException | UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserProfileDetailsResponseDto> register(@RequestBody RegisterRequestDto registerRequestDto) {
        try {
            return ResponseEntity.ok(authService.register(registerRequestDto));
        } catch (AuthProcessingException | UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/refresh/access-token")
    public ResponseEntity<UserProfileDetailsResponseDto> refreshAuthToken(@RequestBody TokenRefreshRequestDto tokenRefreshRequestDto) {
        try {
            return ResponseEntity.ok(authService.refreshAuthToken(tokenRefreshRequestDto));
        } catch (AuthProcessingException | UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @GetMapping("/refresh/confirmation-token/{token}")
    public void refreshConfirmationToken(@PathVariable String token) {
        try {
            authService.refreshConfirmationToken(token);

        } catch (UserProfileProcessingException | ConfirmationTokenProcessingException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/confirm/account/token/{token}")
    public ResponseEntity<UserProfileDetailsResponseDto> confirmAccount(@PathVariable String token) {
        try {
            return ResponseEntity.ok(authService.confirmAccount(token));
        } catch (ConfirmationTokenProcessingException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    @GetMapping("/confirm/changing/password/email/{email}")
    public void confirmChangingPassword(@PathVariable String email) {
        try {
            authService.confirmChangingPassword(email);

        } catch (UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/change/password")
    public void changePassword(@RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        try {
            authService.changePassword(changePasswordRequestDto);

        } catch (AuthProcessingException | ConfirmationTokenProcessingException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }
}
