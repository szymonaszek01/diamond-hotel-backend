package com.app.diamondhotelbackend.dto.openweather.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class FeelsLike {

    private double day;

    private double night;

    private double eve;

    private double morn;
}
