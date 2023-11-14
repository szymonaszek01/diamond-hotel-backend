package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.common.FileResponseDto;
import com.app.diamondhotelbackend.entity.RoomType;
import com.app.diamondhotelbackend.exception.RoomProcessingException;
import com.app.diamondhotelbackend.exception.RoomTypeProcessingException;
import com.app.diamondhotelbackend.service.roomtype.RoomTypeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RequestMapping("/api/v1/room-type")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://diamond-hotel-frontend.vercel.app/"}, allowCredentials = "true")
public class RoomTypeController {

    private final RoomTypeServiceImpl roomTypeService;

    @PostMapping("/create")
    public ResponseEntity<RoomType> createRoomType(@RequestBody RoomType roomType) {
        try {
            return ResponseEntity.ok(roomTypeService.createRoomType(roomType));
        } catch (RoomProcessingException e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<RoomType> getRoomTypeById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(roomTypeService.getRoomTypeById(id));

        } catch (RoomTypeProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<RoomType> getRoomTypeByName(@PathVariable String name) {
        try {
            return ResponseEntity.ok(roomTypeService.getRoomTypeByName(name));

        } catch (RoomTypeProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/all")
    public List<RoomType> getRoomTypeList() {
        return roomTypeService.getRoomTypeList();
    }

    @GetMapping("/all/names")
    public List<String> getRoomTypeNameList() {
        return roomTypeService.getRoomTypeNameList();
    }

    @GetMapping("/id/{id}/equipment")
    public List<String> getRoomTypeEquipmentById(@PathVariable long id) {
        return roomTypeService.getRoomTypeEquipmentById(id);
    }

    @GetMapping("/id/{id}/image")
    public ResponseEntity<FileResponseDto> getRoomTypeImageById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(roomTypeService.getRoomTypeImageById(id));

        } catch (RoomTypeProcessingException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/image")
    public ResponseEntity<FileResponseDto> createRoomTypeImage(@RequestParam("image") MultipartFile file) {
        try {
            return ResponseEntity.ok(roomTypeService.createRoomTypeImage(file));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
