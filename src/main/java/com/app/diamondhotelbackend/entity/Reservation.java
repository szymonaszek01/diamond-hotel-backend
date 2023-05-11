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

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private int numberOfAdults;

    private int numberOfChildren;

    private BigDecimal cost;

    private String status;
}
