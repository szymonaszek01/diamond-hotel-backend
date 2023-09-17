package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.entity.RoomType;
import com.app.diamondhotelbackend.exception.RoomTypeProcessingException;
import com.app.diamondhotelbackend.service.roomtype.RoomTypeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequestMapping("/api/v1/room-type")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
public class RoomTypeController {

    private final RoomTypeServiceImpl roomTypeService;

    @GetMapping("/id/{id}")
    public ResponseEntity<RoomType> getRoomTypeById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(roomTypeService.getRoomTypeById(id));

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
}
