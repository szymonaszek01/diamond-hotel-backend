package com.app.diamondhotelbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty("created_at")
    @Column(nullable = false)
    private Date createdAt;

    @JsonProperty("expires_at")
    @Column(nullable = false)
    private Date expiresAt;

    private BigDecimal cost;

    private String status;

    @Column(unique = true)
    private String token;

    @Column(unique = true)
    private String charge;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "payment", cascade = CascadeType.ALL)
    private Reservation reservation;
}
