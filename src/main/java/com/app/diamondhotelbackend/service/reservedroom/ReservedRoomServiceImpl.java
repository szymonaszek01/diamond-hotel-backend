package com.app.diamondhotelbackend.service.reservedroom;

import com.app.diamondhotelbackend.entity.ReservedRoom;
import com.app.diamondhotelbackend.repository.ReservedRoomRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReservedRoomServiceImpl implements ReservedRoomService {

    private final ReservedRoomRepository reservedRoomRepository;

    @Override
    public List<ReservedRoom> getReservedRoomListByReservationCheckInAndReservationCheckOut(Date checkIn, Date checkOut) {
        return reservedRoomRepository.findAllByReservationCheckInAndReservationCheckOut(checkIn, checkOut);
    }
}
