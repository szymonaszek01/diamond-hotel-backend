package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.auth.LoginRequestDto;
import com.app.diamondhotelbackend.dto.auth.RegisterRequestDto;
import com.app.diamondhotelbackend.dto.auth.UserProfileDetailsResponseDto;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.UserProfileNotFoundException;
import com.app.diamondhotelbackend.service.AuthService;
import com.app.diamondhotelbackend.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api/v1/user-profile")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://diamond-hotel-frontend.vercel.app", "http://localhost:4200", "http://localhost:3000"}, allowCredentials = "true")
public class UserProfileController {

    private final AuthService authService;

    private final UserProfileService userProfileService;

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

    @GetMapping("/id/{id}/details/info")
    public ResponseEntity<UserProfile> getUserProfileDetails(@PathVariable long id) {
        try {
            return ResponseEntity.ok(userProfileService.getUserProfileById(id));
        } catch (UserProfileNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all/info")
    public List<UserProfile> getUserProfileInfoList() {
        return userProfileService.getUserProfileInfoList();
    }

    @DeleteMapping("/id/{id}/delete")
    public void deleteUserProfile(@PathVariable long id) {
        try {
            userProfileService.deleteUserProfile(id);
        } catch (UserProfileNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
