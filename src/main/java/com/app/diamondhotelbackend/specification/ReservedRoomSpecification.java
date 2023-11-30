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

    public static Specification<ReservedRoom> reservationCheckInBetween(Date min, Date max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.join("reservation", JoinType.INNER).get("checkIn"), min, max);
    }

    public static Specification<ReservedRoom> reservationCheckOutBetween(Date min, Date max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.join("reservation", JoinType.INNER).get("checkOut"), min, max);
    }

    public static Specification<ReservedRoom> reservationCheckInAndReservationCheckOutIncludes(Date min, Date max) {
        return (root, query, criteriaBuilder) -> {
            Join<Reservation, ReservedRoom> reservationReservedRoomJoin = root.join("reservation", JoinType.INNER);
            Predicate checkInPredicateMin = criteriaBuilder.lessThanOrEqualTo(reservationReservedRoomJoin.get("checkIn"),  min);
            Predicate checkInPredicateMax = criteriaBuilder.lessThanOrEqualTo(reservationReservedRoomJoin.get("checkIn"), max);
            Predicate checkOutPredicateMin = criteriaBuilder.greaterThanOrEqualTo(reservationReservedRoomJoin.get("checkOut"),  min);
            Predicate checkOutPredicateMax = criteriaBuilder.greaterThanOrEqualTo(reservationReservedRoomJoin.get("checkOut"), max);
            List<Predicate> predicateList = List.of(checkInPredicateMin, checkInPredicateMax, checkOutPredicateMin, checkOutPredicateMax);
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

    public static Specification<ReservedRoom> paymentStatusNotEqual(String paymentStatus) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.join("reservation", JoinType.INNER).join("payment", JoinType.INNER).get("status"), paymentStatus);
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
