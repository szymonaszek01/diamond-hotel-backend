package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.entity.ReservedRoom;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.service.reservedroom.ReservedRoomServiceImpl;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequestMapping("/api/v1/reserved-room")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://diamond-hotel-frontend.vercel.app/"}, allowCredentials = "true")
public class ReservedRoomController {

    private final ReservedRoomServiceImpl reservedRoomService;

    @GetMapping("/all")
    public List<ReservedRoom> getReservedRoomList(@RequestParam(value = "page") int page,
                                                  @RequestParam(value = "size") int size,
                                                  @RequestParam(value = "filters", defaultValue = "{}", required = false) JSONObject filters,
                                                  @RequestParam(value = "sort", defaultValue = "[]", required = false) JSONArray sort) {
        return reservedRoomService.getReservedRoomList(page, size, filters, sort);
    }

    @GetMapping("/all/user-profile-id/{userProfileId}")
    public List<ReservedRoom> getReservedRoomListByUserProfileId(@PathVariable long userProfileId,
                                                                 @RequestParam(value = "page") int page,
                                                                 @RequestParam(value = "size") int size,
                                                                 @RequestParam(value = "filters", defaultValue = "{}", required = false) JSONObject filters,
                                                                 @RequestParam(value = "sort", defaultValue = "[]", required = false) JSONArray sort) {
        return reservedRoomService.getReservedRoomListByUserProfileId(userProfileId, page, size, filters, sort);
    }

    @GetMapping("/all/number")
    public ResponseEntity<Long> countReservedRoomList() {
        try {
            return ResponseEntity.ok(reservedRoomService.countReservedRoomList());
        } catch (UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/all/number/user-profile-id/{userProfileId}")
    public ResponseEntity<Long> countReservedRoomListByUserProfileId(@PathVariable long userProfileId) {
        try {
            return ResponseEntity.ok(reservedRoomService.countReservedRoomListByUserProfileId(userProfileId));
        } catch (UserProfileProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
