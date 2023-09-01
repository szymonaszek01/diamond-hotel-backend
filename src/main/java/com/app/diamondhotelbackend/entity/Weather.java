package com.app.diamondhotelbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonIgnore
    @Column(nullable = false)
    private Date createdAt;

    @JsonProperty("measured_at")
    private Date measuredAt;

    private String main;

    private String description;

    private String icon;

    @JsonProperty("temperature_day")
    private double temperatureDay;

    @JsonProperty("temperature_night")
    private double temperatureNight;

    private int pressure;

    private double wind;

    private int humidity;
}
