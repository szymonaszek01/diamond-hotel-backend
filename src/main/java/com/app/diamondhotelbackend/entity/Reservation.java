package com.app.diamondhotelbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

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

    private Date checkIn;

    private Date checkOut;

    private int adults;

    private int children;

    @ManyToOne
    private UserProfile userProfile;

    @ManyToOne
    private Flight flight;

    @OneToOne
    private Transaction transaction;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<ReservedRoom> reservedRoomList;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<CarRent> carRentList;
}
