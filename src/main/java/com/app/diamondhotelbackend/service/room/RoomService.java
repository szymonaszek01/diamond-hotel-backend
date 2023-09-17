package com.app.diamondhotelbackend.service.room;

import com.app.diamondhotelbackend.dto.room.response.RoomAvailableResponseDto;
import com.app.diamondhotelbackend.dto.room.response.RoomSelectedCostResponseDto;
import com.app.diamondhotelbackend.exception.RoomProcessingException;

import java.util.List;

public interface RoomService {

    RoomAvailableResponseDto getRoomAvailableList(String checkIn, String checkOut, int rooms, int adults, int children, List<Long> roomTypeIdList, double pricePerHotelNight) throws RoomProcessingException;

    RoomSelectedCostResponseDto getRoomSelectedCost(String checkIn, String checkOut, long roomTypeId, int rooms);
}
