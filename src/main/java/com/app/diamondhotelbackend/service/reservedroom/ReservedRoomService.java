package com.app.diamondhotelbackend.service.reservedroom;

import com.app.diamondhotelbackend.entity.ReservedRoom;

import java.util.Date;
import java.util.List;

public interface ReservedRoomService {

    List<ReservedRoom> getReservedRoomListByReservationCheckInAndReservationCheckOut(Date checkIn, Date checkOut);
}
