package com.app.diamondhotelbackend.service.reservedroom;

import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.ReservedRoom;
import com.app.diamondhotelbackend.entity.Room;

import java.sql.Date;
import java.util.List;

public interface ReservedRoomService {

    ReservedRoom createReservedRoom(Reservation reservation, Room room);

    List<ReservedRoom> getReservedRoomListByReservationId(long reservationId);

    List<ReservedRoom> getReservedRoomListByReservationCheckInAndReservationCheckOut(Date checkIn, Date checkOut);
}
