package com.app.diamondhotelbackend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class CarRent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty("created_at")
    @Column(nullable = false)
    private Date startedAt;

    @JsonProperty("expires_at")
    @Column(nullable = false)
    private Date expiresAt;

    @Column(unique = true)
    private String plate;

    private String brand;

    private String model;

    private byte[] picture;

    private int passengers;

    private BigDecimal cost;

    @ManyToOne
    private Reservation reservation;
}
