package com.app.diamondhotelbackend.service.reservedroom;

import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.ReservedRoom;
import com.app.diamondhotelbackend.entity.Room;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.repository.ReservedRoomRepository;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.util.DateUtil;
import com.app.diamondhotelbackend.util.UrlUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReservedRoomServiceImpl implements ReservedRoomService {

    private final ReservedRoomRepository reservedRoomRepository;

    private final UserProfileServiceImpl userProfileService;

    @Override
    public ReservedRoom createReservedRoom(Reservation reservation, Room room) {
        long duration = DateUtil.getDifferenceBetweenTwoDates(reservation.getCheckIn(), reservation.getCheckOut());
        BigDecimal cost = BigDecimal.valueOf(room.getRoomType().getPricePerHotelNight().longValue() * duration);
        int occupation = 0;
        ReservedRoom reservedRoom = ReservedRoom.builder().occupied(occupation).cost(cost).room(room).reservation(reservation).build();

        return reservedRoomRepository.save(reservedRoom);
    }

    @Override
    public List<ReservedRoom> getReservedRoomList(int page, int size, String paymentStatus, JSONArray jsonArray) {
        if (page < 0 || size < 1) {
            return Collections.emptyList();
        }

        List<Sort.Order> orderList = UrlUtil.toOrderListMapper(jsonArray);
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderList));
        Page<ReservedRoom> reservedRoomPage;
        if (paymentStatus.isEmpty()) {
            reservedRoomPage = reservedRoomRepository.findAll(pageable);
        } else {
            reservedRoomPage = reservedRoomRepository.findAllByReservationPaymentStatus(paymentStatus, pageable);
        }

        return reservedRoomPage.getContent();
    }

    @Override
    public List<ReservedRoom> getReservedRoomListByReservationCheckInAndReservationCheckOut(Date checkIn, Date checkOut) {
        return reservedRoomRepository.findAllByReservationCheckInAndReservationCheckOut(checkIn, checkOut);
    }

    @Override
    public List<ReservedRoom> getReservedRoomListByReservationId(long reservationId) {
        return reservedRoomRepository.findAllByReservationId(reservationId);
    }

    @Override
    public List<ReservedRoom> getReservedRoomListByUserProfileId(long userProfileId, int page, int size, String paymentStatus, JSONArray jsonArray) {
        if (page < 0 || size < 1) {
            return Collections.emptyList();
        }

        List<Sort.Order> orderList = UrlUtil.toOrderListMapper(jsonArray);
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderList));
        Page<ReservedRoom> reservedRoomPage;
        if (paymentStatus.isEmpty()) {
            reservedRoomPage = reservedRoomRepository.findAllByReservationUserProfileId(userProfileId, pageable);
        } else {
            reservedRoomPage = reservedRoomRepository.findAllByReservationUserProfileIdAndReservationPaymentStatus(userProfileId, paymentStatus, pageable);
        }

        return reservedRoomPage.getContent();
    }

    @Override
    public Long countReservedRoomListByUserProfileId(long userProfileId) throws UserProfileProcessingException {
        UserProfile userProfile = userProfileService.getUserProfileById(userProfileId);

        return reservedRoomRepository.countAllByReservationUserProfile(userProfile);
    }
}
