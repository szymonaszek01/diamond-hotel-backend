package com.app.diamondhotelbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Room room;

    @ManyToOne
    private UserProfile userProfile;

    @ManyToOne
    private Flight flight;

    private String code;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private BigDecimal totalWithoutTax;

    private BigDecimal tax;

    private BigDecimal carRent;

    private BigDecimal carPickUp;

    private String status;
}
