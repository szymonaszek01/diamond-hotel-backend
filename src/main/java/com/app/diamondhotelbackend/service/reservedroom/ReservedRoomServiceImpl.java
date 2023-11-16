package com.app.diamondhotelbackend.service.reservedroom;

import com.app.diamondhotelbackend.dto.table.model.ReservationPaymentReservedRoomTableFilter;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.ReservedRoom;
import com.app.diamondhotelbackend.entity.Room;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.repository.ReservedRoomRepository;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.DateUtil;
import com.app.diamondhotelbackend.util.UrlUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collections;
import java.util.List;

import static com.app.diamondhotelbackend.specification.ReservedRoomSpecification.*;

@Service
@AllArgsConstructor
@Slf4j
public class ReservedRoomServiceImpl implements ReservedRoomService {

    private static final long DAY_IN_MILLIS = 24 * 60 * 60 * 1000;
    private final ReservedRoomRepository reservedRoomRepository;

    @Override
    public ReservedRoom createReservedRoom(Reservation reservation, Room room) {
        long duration = DateUtil.getDifferenceBetweenTwoDates(reservation.getCheckIn(), reservation.getCheckOut());
        BigDecimal cost = BigDecimal.valueOf(room.getRoomType().getPricePerHotelNight().longValue() * duration);
        int occupation = 0;
        ReservedRoom reservedRoom = ReservedRoom.builder().occupied(occupation).cost(cost).room(room).reservation(reservation).build();

        return reservedRoomRepository.save(reservedRoom);
    }

    @Override
    public List<ReservedRoom> getReservedRoomList(int page, int size, JSONObject filters, JSONArray sort) {
        if (page < 0 || size < 1) {
            return Collections.emptyList();
        }

        return prepareReservedRoomList(0, page, size, filters, sort);
    }

    @Override
    public List<ReservedRoom> getReservedRoomListByReservationCheckInAndReservationCheckOut(Date checkIn, Date checkOut) {
        Specification<ReservedRoom> reservedRoomSpecification = Specification.where(paymentStatusNotEqual(ConstantUtil.CANCELLED))
                .and(Specification.where(reservationCheckInOrReservationCheckOutBetween(checkIn, checkOut)).or(reservationCheckInAndReservationCheckOutIncludes(checkIn, checkOut)));
        return reservedRoomRepository.findAll(reservedRoomSpecification);
    }

    @Override
    public List<ReservedRoom> getReservedRoomListByReservationId(long reservationId) {
        Specification<ReservedRoom> reservedRoomSpecification = Specification.where(reservationIdEqual(reservationId));

        return reservedRoomRepository.findAll(reservedRoomSpecification);
    }

    @Override
    public List<ReservedRoom> getReservedRoomListByUserProfileId(long userProfileId, int page, int size, JSONObject filters, JSONArray sort) {
        if (page < 0 || size < 1) {
            return Collections.emptyList();
        }

        return prepareReservedRoomList(userProfileId, page, size, filters, sort);
    }

    @Override
    public List<ReservedRoom> getReservedRoomListByFloor(int floor) {
        long currentDate = new Date(System.currentTimeMillis()).getTime();
        Date min = new Date(currentDate - currentDate % (DAY_IN_MILLIS));
        Date max = new Date(min.getTime() + (DAY_IN_MILLIS));
        Specification<ReservedRoom> reservedRoomSpecification = Specification.where(roomFloorEqual(floor))
                .and(reservationCheckInAndReservationCheckOutIncludes(min, max));

        return reservedRoomRepository.findAll(reservedRoomSpecification);
    }

    @Override
    public Long countReservedRoomList() {
        return reservedRoomRepository.count();
    }

    @Override
    public Long countReservedRoomListByUserProfileId(long userProfileId) throws UserProfileProcessingException {
        Specification<ReservedRoom> reservedRoomSpecification = Specification.where(userProfileIdEqual(userProfileId));

        return reservedRoomRepository.count(reservedRoomSpecification);
    }

    private List<ReservedRoom> prepareReservedRoomList(long userProfileId, int page, int size, JSONObject filters, JSONArray sort) {
        ReservationPaymentReservedRoomTableFilter tableFilters = new ReservationPaymentReservedRoomTableFilter(filters);
        Specification<ReservedRoom> reservedRoomSpecification = Specification.where(userProfileId == 0 ? null : userProfileIdEqual(userProfileId))
                .and(tableFilters.getMinDate() == null || tableFilters.getMaxDate() == null ? null : reservationCheckInAndReservationCheckOutBetween(tableFilters.getMinDate(), tableFilters.getMaxDate()))
                .and(tableFilters.getUserProfileEmail().isEmpty() ? null : userProfileEmailEqual(tableFilters.getUserProfileEmail()))
                .and(tableFilters.getFlightNumber().isEmpty() ? null : flightNumberEqual(tableFilters.getFlightNumber()))
                .and(tableFilters.getPaymentStatus().isEmpty() ? null : paymentStatusEqual(tableFilters.getPaymentStatus()))
                .and(tableFilters.getMinPaymentCost() == null || tableFilters.getMaxPaymentCost() == null ? null : paymentCostBetween(tableFilters.getMinPaymentCost(), tableFilters.getMaxPaymentCost()))
                .and(tableFilters.getPaymentCharge().isEmpty() ? null : paymentChargeEqual(tableFilters.getPaymentCharge()))
                .and(tableFilters.getRoomTypeName().isEmpty() ? null : roomTypeNameEqual(tableFilters.getRoomTypeName()));

        List<Sort.Order> orderList = UrlUtil.toOrderListMapper(sort);
        Pageable pageable = PageRequest.of(page, size, Sort.by(orderList));

        return reservedRoomRepository.findAll(reservedRoomSpecification, pageable).getContent();
    }
}
