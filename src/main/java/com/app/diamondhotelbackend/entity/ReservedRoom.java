package com.app.diamondhotelbackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class ReservedRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int occupied;

    private BigDecimal cost;

    @ManyToOne()
    private Room room;

    @ManyToOne()
    private Reservation reservation;
}
