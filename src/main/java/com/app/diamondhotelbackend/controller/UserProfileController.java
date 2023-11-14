package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.common.FileResponseDto;
import com.app.diamondhotelbackend.dto.userprofile.request.UserProfileDetailsUpdateRequestDto;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RequestMapping("/api/v1/user-profile")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://diamond-hotel-frontend.vercel.app/"}, allowCredentials = "true")
public class UserProfileController {

    private final UserProfileServiceImpl userProfileService;

    @GetMapping("/id/{id}")
    public ResponseEntity<UserProfile> getUserProfileById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(userProfileService.getUserProfileById(id));
        } catch (UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserProfile> getUserProfileByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(userProfileService.getUserProfileByEmail(email));
        } catch (UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<UserProfile> getUserProfileList() {
        return userProfileService.getUserProfileList();
    }

    @GetMapping("/email/{email}/picture")
    public ResponseEntity<FileResponseDto> getUserProfilePictureByEmail(@PathVariable String email) {
        try {
            return ResponseEntity.ok(userProfileService.getUserProfilePictureByEmail(email));
        } catch (UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PutMapping("/email/{email}/details")
    public ResponseEntity<UserProfile> updateUserProfile(@PathVariable String email, @RequestBody UserProfileDetailsUpdateRequestDto userProfileDetailsUpdateRequestDto) {
        try {
            return ResponseEntity.ok(userProfileService.updateUserProfile(email, userProfileDetailsUpdateRequestDto));
        } catch (UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/email/{email}/picture")
    public ResponseEntity<FileResponseDto> updateUserProfilePicture(@PathVariable String email, @RequestParam("image") MultipartFile file) {
        try {
            return ResponseEntity.ok(userProfileService.updateUserProfilePicture(file, email));
        } catch (IOException | UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<UserProfile> deleteUserProfile(@PathVariable long id) {
        try {
            return ResponseEntity.ok(userProfileService.deleteUserProfile(id));
        } catch (UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
