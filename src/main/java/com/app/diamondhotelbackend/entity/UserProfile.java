package com.app.diamondhotelbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String firstname;

    private String lastname;

    private int age;

    private String country;

    @JsonProperty("passport_number")
    @Column(unique = true)
    private String passportNumber;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String city;

    private String street;

    @JsonProperty("postal_code")
    private String postalCode;

    private String role;

    @JsonProperty("auth_provider")
    private String authProvider;

    private String picture;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userProfile", cascade = CascadeType.ALL)
    private List<RoomTypeOpinion> roomTypeOpinionList;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "userProfile", cascade = CascadeType.ALL)
    private List<Reservation> reservationList;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "userProfile", cascade = CascadeType.ALL)
    private Token token;
}
