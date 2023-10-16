package com.app.diamondhotelbackend.service.reservedroom;

import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.ReservedRoom;
import com.app.diamondhotelbackend.entity.Room;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;

import java.sql.Date;
import java.util.List;

public interface ReservedRoomService {

    ReservedRoom createReservedRoom(Reservation reservation, Room room);

    List<ReservedRoom> getReservedRoomListByReservationCheckInAndReservationCheckOut(Date checkIn, Date checkOut);

    List<ReservedRoom> getReservedRoomListByReservationId(long reservationId);

    List<ReservedRoom> getReservedRoomListByUserProfileId(long userProfileId, int page, int size, String paymentStatus);

    Long countReservedRoomListByUserProfileId(long userProfileId) throws UserProfileProcessingException;
}
