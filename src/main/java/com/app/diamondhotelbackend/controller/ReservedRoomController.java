package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.entity.ReservedRoom;
import com.app.diamondhotelbackend.service.reservedroom.ReservedRoomServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/reserved-room")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://diamond-hotel-frontend-react.vercel.app/"}, allowCredentials = "true")
public class ReservedRoomController {

    private final ReservedRoomServiceImpl reservedRoomService;

    @GetMapping("/all/reservation/id/{id}")
    public List<ReservedRoom> getReservedRoomListByReservationId(@PathVariable long id) {
        return reservedRoomService.getReservedRoomListByReservationId(id);
    }
}
