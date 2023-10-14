package com.app.diamondhotelbackend.service.reservation;

import com.app.diamondhotelbackend.dto.reservation.request.ReservationCreateRequestDto;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.exception.ReservationProcessingException;
import com.app.diamondhotelbackend.exception.RoomProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.util.List;

public interface ReservationService {

    Reservation createReservation(ReservationCreateRequestDto reservationCreateRequestDto) throws ReservationProcessingException, UserProfileProcessingException, RoomProcessingException, IOException;

    List<Reservation> getReservationList(int page, int size);

    List<Reservation> getReservationListByUserProfileId(long userProfileId, int page, int size, String paymentStatus);

    Long countReservationListByUserProfileId(long userProfileId) throws UserProfileProcessingException;

    Reservation getReservationById(long id) throws ReservationProcessingException;

    InputStreamResource getReservationPdfDocument(long id) throws ReservationProcessingException;
}
