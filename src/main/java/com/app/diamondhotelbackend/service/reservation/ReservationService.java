package com.app.diamondhotelbackend.service.reservation;

import com.app.diamondhotelbackend.dto.common.FileResponseDto;
import com.app.diamondhotelbackend.dto.reservation.request.ReservationCreateRequestDto;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.exception.ReservationProcessingException;
import com.app.diamondhotelbackend.exception.RoomProcessingException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.stripe.exception.StripeException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

public interface ReservationService {

    Reservation createReservation(ReservationCreateRequestDto reservationCreateRequestDto) throws ReservationProcessingException, UserProfileProcessingException, RoomProcessingException, IOException, StripeException;

    List<Reservation> getReservationList(int page, int size, JSONObject filters, JSONArray sort);

    List<Reservation> getReservationList(Date min, Date max);

    List<Reservation> getReservationListByUserProfileId(long userProfileId, int page, int size, JSONObject filters, JSONArray sort);

    List<Integer> getReservationCheckInAndCheckOutYearList();

    Reservation getReservationById(long id) throws ReservationProcessingException;

    FileResponseDto getReservationPdfDocumentById(long id) throws ReservationProcessingException, IOException;

    Long countReservationList();

    Long countReservationListByUserProfileId(long userProfileId) throws UserProfileProcessingException;

    Reservation updateReservationPayment(long id, String paymentToken);

    Reservation deleteReservationById(long id) throws ReservationProcessingException;
}
