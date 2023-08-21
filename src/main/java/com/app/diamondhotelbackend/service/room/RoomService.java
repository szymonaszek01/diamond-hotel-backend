package com.app.diamondhotelbackend.service.room;

import com.app.diamondhotelbackend.dto.room.RoomDto;
import com.app.diamondhotelbackend.dto.shoppingcart.RoomTypeInfoDto;
import com.app.diamondhotelbackend.entity.Room;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomService {

    List<Room> getRoomListByRoomTypeNameCheckInAndCheckOut(String roomTypeName, LocalDateTime checkIn, LocalDateTime checkOut);

    List<Room> getRoomListByRoomTypeName(String roomTypeName);

    RoomDto toRoomDtoMapper(Room room);

    boolean isMismatchBetweenSelectedAndAvailableRooms(List<RoomTypeInfoDto> roomTypeInfoDtoList, LocalDateTime checkIn, LocalDateTime checkOut);
}
