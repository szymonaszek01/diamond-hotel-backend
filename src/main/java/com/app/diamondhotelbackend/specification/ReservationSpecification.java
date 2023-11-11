package com.app.diamondhotelbackend.specification;

import com.app.diamondhotelbackend.entity.Reservation;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class ReservationSpecification {

    public static Specification<Reservation> reservationCheckInReservationCheckOutBetween(Date min, Date max) {
        return (root, query, criteriaBuilder) -> {
            Predicate checkInPredicate = criteriaBuilder.between(root.get("checkIn"), min, max);
            Predicate checkOutPredicate = criteriaBuilder.between(root.get("checkOut"), min, max);
            List<Predicate> predicateList = List.of(checkInPredicate, checkOutPredicate);
            return criteriaBuilder.and(predicateList.toArray(Predicate[]::new));
        };
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
