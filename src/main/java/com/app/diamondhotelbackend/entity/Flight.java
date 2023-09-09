package com.app.diamondhotelbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String flightNumber;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flight", cascade = CascadeType.ALL)
    private List<Reservation> reservationList;
}
