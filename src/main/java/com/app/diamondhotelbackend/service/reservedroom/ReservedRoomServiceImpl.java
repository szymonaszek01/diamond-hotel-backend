package com.app.diamondhotelbackend.service.reservedroom;

import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.ReservedRoom;
import com.app.diamondhotelbackend.entity.Room;
import com.app.diamondhotelbackend.repository.ReservedRoomRepository;
import com.app.diamondhotelbackend.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReservedRoomServiceImpl implements ReservedRoomService {

    private final ReservedRoomRepository reservedRoomRepository;

    @Override
    public ReservedRoom createReservedRoom(Reservation reservation, Room room) {
        long duration = DateUtil.getDifferenceBetweenTwoDates(reservation.getCheckIn(), reservation.getCheckOut());
        BigDecimal cost = BigDecimal.valueOf(room.getRoomType().getPricePerHotelNight().longValue() * duration);
        int occupation = 0;
        ReservedRoom reservedRoom = ReservedRoom.builder()
                .occupied(occupation)
                .cost(cost)
                .room(room)
                .reservation(reservation)
                .build();

        return reservedRoomRepository.save(reservedRoom);
    }

    @Override
    public List<ReservedRoom> getReservedRoomListByReservationId(long reservationId) {
        return reservedRoomRepository.findAllByReservationId(reservationId);
    }

    @Override
    public List<ReservedRoom> getReservedRoomListByReservationCheckInAndReservationCheckOut(Date checkIn, Date checkOut) {
        return reservedRoomRepository.findAllByReservationCheckInAndReservationCheckOut(checkIn, checkOut);
    }
}
