package com.app.diamondhotelbackend.repository.specification;

import com.app.diamondhotelbackend.entity.Reservation;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.sql.Date;

public class ReservationSpecification {

    public static Specification<Reservation> reservationCheckInBetween(Date min, Date max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("checkIn"), min, max);
    }

    public static Specification<Reservation> reservationCheckOutBetween(Date min, Date max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("checkOut"), min, max);
    }

    public static Specification<Reservation> userProfileIdEqual(long userProfileId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("userProfile", JoinType.INNER).get("id"), userProfileId);
    }

    public static Specification<Reservation> userProfileEmailEqual(String userProfileEmail) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("userProfile", JoinType.INNER).get("email"), userProfileEmail);
    }

    public static Specification<Reservation> flightNumberEqual(String flightNumber) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("flight", JoinType.INNER).get("flightNumber"), flightNumber);
    }

    public static Specification<Reservation> paymentStatusEqual(String paymentStatus) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("payment", JoinType.INNER).get("status"), paymentStatus);
    }

    public static Specification<Reservation> paymentStatusNotEqual(String paymentStatus) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.join("payment", JoinType.INNER).get("status"), paymentStatus);
    }

    public static Specification<Reservation> paymentCostBetween(BigDecimal min, BigDecimal max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.join("payment", JoinType.INNER).get("cost"), min, max);
    }

    public static Specification<Reservation> paymentChargeEqual(String paymentCharge) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("payment", JoinType.INNER).get("charge"), paymentCharge);
    }

    public static Specification<Reservation> roomTypeNameEqual(String roomTypeName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("reservedRoomList", JoinType.INNER).join("room").join("roomType").get("name"), roomTypeName);
    }
}
