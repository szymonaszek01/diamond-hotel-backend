package com.app.diamondhotelbackend.service.reservation;

import com.app.diamondhotelbackend.dto.reservation.request.ReservationCreateRequestDto;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.exception.ReservationProcessingException;
import com.app.diamondhotelbackend.exception.RoomProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;

public interface ReservationService {

    Reservation createReservation(ReservationCreateRequestDto reservationCreateRequestDto) throws ReservationProcessingException, UserProfileProcessingException, RoomProcessingException;

    Reservation getReservationById(long id) throws ReservationProcessingException;
}
