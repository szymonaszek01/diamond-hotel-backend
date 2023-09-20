package com.app.diamondhotelbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
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

    @JsonProperty("check_in")
    private Date checkIn;

    @JsonProperty("check_out")
    private Date checkOut;

    private int adults;

    private int children;

    @JsonProperty("user_profile")
    @ManyToOne
    private UserProfile userProfile;

    @ManyToOne
    private Flight flight;

    @OneToOne
    private Payment payment;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<ReservedRoom> reservedRoomList;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<CarRent> carRentList;
}
