package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.entity.RoomType;
import com.app.diamondhotelbackend.service.roomtype.RoomTypeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/room-type")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
public class RoomTypeController {

    private final RoomTypeServiceImpl roomTypeService;

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
