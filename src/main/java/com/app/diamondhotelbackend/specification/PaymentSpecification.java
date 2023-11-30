package com.app.diamondhotelbackend.specification;

import com.app.diamondhotelbackend.entity.Payment;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.sql.Date;

public class PaymentSpecification {

    public static Specification<Payment> reservationCheckInBetween(Date min, Date max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.join("reservation", JoinType.INNER).get("checkIn"), min, max);
    }

    public static Specification<Payment> reservationCheckOutBetween(Date min, Date max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.join("reservation", JoinType.INNER).get("checkOut"), min, max);
    }

    public static Specification<Payment> userProfileIdEqual(long userProfileId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("reservation", JoinType.INNER).join("userProfile", JoinType.INNER).get("id"), userProfileId);
    }

    public static Specification<Payment> userProfileEmailEqual(String userProfileEmail) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("reservation", JoinType.INNER).join("userProfile", JoinType.INNER).get("email"), userProfileEmail);
    }

    public static Specification<Payment> flightNumberEqual(String flightNumber) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("reservation", JoinType.INNER).join("flightNumber", JoinType.INNER).get("number"), flightNumber);
    }

    public static Specification<Payment> paymentStatusEqual(String paymentStatus) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), paymentStatus);
    }

    public static Specification<Payment> paymentStatusNotEqual(String paymentStatus) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("status"), paymentStatus);
    }

    public static Specification<Payment> paymentCostBetween(BigDecimal min, BigDecimal max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("cost"), min, max);
    }

    public static Specification<Payment> paymentChargeEqual(String paymentCharge) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("charge"), paymentCharge);
    }

    public static Specification<Payment> roomTypeNameEqual(String roomTypeName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("reservation", JoinType.INNER).join("reservedRoomList", JoinType.INNER).join("room", JoinType.INNER).join("roomType", JoinType.INNER).get("name"), roomTypeName);
    }

    public static Specification<Payment> paymentCreatedAtBetween(java.util.Date min, java.util.Date max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("createdAt"), min, max);
    }
}
