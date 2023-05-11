package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.LoginRequestDto;
import com.app.diamondhotelbackend.dto.RegisterRequestDto;
import com.app.diamondhotelbackend.dto.UserProfileDetailsResponseDto;
import com.app.diamondhotelbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RequestMapping("/api/v1/user-profile")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "https://diamond-hotel-frontend.vercel.app")
public class UserProfileController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserProfileDetailsResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        Optional<UserProfileDetailsResponseDto> result = authService.login(loginRequestDto);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(result.get());
    }

    @PostMapping("/register")
    public ResponseEntity<UserProfileDetailsResponseDto> register(@RequestBody RegisterRequestDto registerRequestDto) {
        Optional<UserProfileDetailsResponseDto> result = authService.register(registerRequestDto);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok(result.get());
    }
}
