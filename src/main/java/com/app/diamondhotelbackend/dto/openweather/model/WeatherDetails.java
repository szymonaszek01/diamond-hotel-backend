package com.app.diamondhotelbackend.dto.openweather.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class WeatherDetails {

    private long id;

    private String main;

    private String description;

    private String icon;
}
