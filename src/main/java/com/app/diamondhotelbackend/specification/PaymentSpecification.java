package com.app.diamondhotelbackend.specification;

import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.entity.Reservation;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class PaymentSpecification {

    public static Specification<Payment> reservationCheckInReservationCheckOutBetween(Date min, Date max) {
        return (root, query, criteriaBuilder) -> {
            Join<Reservation, Payment> reservationPaymentJoin = root.join("reservation", JoinType.INNER);
            Predicate checkInPredicate = criteriaBuilder.between(reservationPaymentJoin.get("checkIn"), min, max);
            Predicate checkOutPredicate = criteriaBuilder.between(reservationPaymentJoin.get("checkOut"), min, max);
            List<Predicate> predicateList = List.of(checkInPredicate, checkOutPredicate);
            return criteriaBuilder.and(predicateList.toArray(Predicate[]::new));
        };
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

    public static Specification<Payment> paymentCostBetween(BigDecimal min, BigDecimal max) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("cost"), min, max);
    }

    public static Specification<Payment> paymentChargeEqual(String paymentCharge) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("charge"), paymentCharge);
    }

    public static Specification<Payment> roomTypeNameEqual(String roomTypeName) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join("reservation", JoinType.INNER).join("reservedRoomList", JoinType.INNER).join("room", JoinType.INNER).join("roomType", JoinType.INNER).get("name"), roomTypeName);
    }
}
