package com.app.diamondhotelbackend.service.reservation;

import com.app.diamondhotelbackend.dto.common.PdfResponseDto;
import com.app.diamondhotelbackend.dto.reservation.request.ReservationCreateRequestDto;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.exception.ReservationProcessingException;
import com.app.diamondhotelbackend.exception.RoomProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.stripe.exception.StripeException;
import org.json.JSONArray;

import java.io.IOException;
import java.util.List;

public interface ReservationService {

    Reservation createReservation(ReservationCreateRequestDto reservationCreateRequestDto) throws ReservationProcessingException, UserProfileProcessingException, RoomProcessingException, IOException, StripeException;

    List<Reservation> getReservationList(int page, int size, String paymentStatus, JSONArray jsonArray);

    List<Reservation> getReservationListByUserProfileId(long userProfileId, int page, int size, String paymentStatus, JSONArray jsonArray);

    Reservation getReservationById(long id) throws ReservationProcessingException;

    PdfResponseDto getReservationPdfDocumentById(long id) throws ReservationProcessingException, IOException;

    Long countReservationList();

    Long countReservationListByUserProfileId(long userProfileId) throws UserProfileProcessingException;

    Reservation updateReservationPayment(long id, String paymentToken);

    Reservation deleteReservationById(long id) throws ReservationProcessingException;
}
