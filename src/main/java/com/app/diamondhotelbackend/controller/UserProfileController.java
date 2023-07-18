package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequestMapping("/api/v1/user-profile")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"https://diamond-hotel-frontend.vercel.app", "http://localhost:4200", "http://localhost:3000"}, allowCredentials = "true")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/id/{id}/details/info")
    public ResponseEntity<UserProfile> getUserProfileDetails(@PathVariable long id) {
        try {
            return ResponseEntity.ok(userProfileService.getUserProfileById(id));
        } catch (UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/email/{email}/details/info")
    public ResponseEntity<UserProfile> getUserProfileDetails(@PathVariable String email) {
        try {
            return ResponseEntity.ok(userProfileService.getUserProfileByEmail(email));
        } catch (UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
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
        } catch (UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
