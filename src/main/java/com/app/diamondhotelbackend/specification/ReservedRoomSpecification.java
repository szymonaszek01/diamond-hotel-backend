package com.app.diamondhotelbackend.specification;

import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.ReservedRoom;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class ReservedRoomSpecification {

    public static Specification<ReservedRoom> reservationIdEqual(long reservationId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("reservation", JoinType.INNER).get("id"), reservationId);
    }

    public static Specification<ReservedRoom> reservationCheckInReservationCheckOutBetween(Date min, Date max) {
        return (root, query, criteriaBuilder) -> {
            Join<Reservation, ReservedRoom> reservationReservedRoomJoin = root.join("reservation", JoinType.INNER);
            Predicate checkInPredicate = criteriaBuilder.between(reservationReservedRoomJoin.get("checkIn"), min, max);
            Predicate checkOutPredicate = criteriaBuilder.between(reservationReservedRoomJoin.get("checkOut"), min, max);
            List<Predicate> predicateList = List.of(checkInPredicate, checkOutPredicate);
            return criteriaBuilder.and(predicateList.toArray(Predicate[]::new));
        };
    }

    public static Specification<ReservedRoom> reservationCheckInReservationCheckOutIncludes(Date min, Date max) {
        return (root, query, criteriaBuilder) -> {
            Join<Reservation, ReservedRoom> reservationReservedRoomJoin = root.join("reservation", JoinType.INNER);
            Predicate checkInPredicate = criteriaBuilder.lessThanOrEqualTo(reservationReservedRoomJoin.get("checkIn"),  min);
            Predicate checkOutPredicate = criteriaBuilder.greaterThanOrEqualTo(reservationReservedRoomJoin.get("checkOut"), max);
            List<Predicate> predicateList = List.of(checkInPredicate, checkOutPredicate);
            return criteriaBuilder.and(predicateList.toArray(Predicate[]::new));
        };
    }

    public static Specification<ReservedRoom> userProfileIdEqual(long userProfileId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("reservation", JoinType.INNER).join("userProfile", JoinType.INNER).get("id"), userProfileId);
    }

    public static Specification<ReservedRoom> userProfileEmailEqual(String userProfileEmail) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("reservation", JoinType.INNER).join("userProfile", JoinType.INNER).get("email"), userProfileEmail);
    }

    public static Specification<ReservedRoom> flightNumberEqual(String flightNumber) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("reservation", JoinType.INNER).join("flight", JoinType.INNER).get("flightNumber"), flightNumber);
    }

    public static Specification<ReservedRoom> paymentStatusEqual(String paymentStatus) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("reservation", JoinType.INNER).join("payment", JoinType.INNER).get("status"), paymentStatus);
    }

    public static Specification<ReservedRoom> paymentCostBetween(BigDecimal min, BigDecimal max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.join("reservation", JoinType.INNER).join("payment", JoinType.INNER).get("cost"), min, max);
    }

    public static Specification<ReservedRoom> paymentChargeEqual(String paymentCharge) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("reservation", JoinType.INNER).join("payment", JoinType.INNER).get("charge"), paymentCharge);
    }

    public static Specification<ReservedRoom> roomTypeNameEqual(String roomTypeName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("room", JoinType.INNER).join("roomType", JoinType.INNER).get("name"), roomTypeName);
    }

    public static Specification<ReservedRoom> roomFloorEqual(int roomFloor) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("room", JoinType.INNER).get("floor"), roomFloor);
    }
}
