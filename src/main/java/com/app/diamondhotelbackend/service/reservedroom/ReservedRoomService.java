package com.app.diamondhotelbackend.service.reservedroom;

import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.ReservedRoom;
import com.app.diamondhotelbackend.entity.Room;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.List;

public interface ReservedRoomService {

    ReservedRoom createReservedRoom(Reservation reservation, Room room);

    List<ReservedRoom> getReservedRoomList(int page, int size, JSONObject filters, JSONArray sort);

    List<ReservedRoom> getReservedRoomListByReservationCheckInAndReservationCheckOut(Date checkIn, Date checkOut);

    List<ReservedRoom> getReservedRoomListByReservationId(long reservationId);

    List<ReservedRoom> getReservedRoomListByUserProfileId(long userProfileId, int page, int size, JSONObject filters, JSONArray sort);

    List<ReservedRoom> getReservedRoomListByFloor(int floor);

    Long countReservedRoomList();

    Long countReservedRoomListByUserProfileId(long userProfileId) throws UserProfileProcessingException;
}
