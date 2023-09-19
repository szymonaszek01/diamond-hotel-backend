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
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty("created_at")
    @Column(nullable = false)
    private Date createdAt;

    @JsonProperty("expires_at")
    @Column(nullable = false)
    private Date expiresAt;

    @Column(unique = true)
    private String token;

    private BigDecimal cost;

    private double tax;

    private String status;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "transaction", cascade = CascadeType.ALL)
    private Reservation reservation;
}
