package com.app.diamondhotelbackend.dto.openweather.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Temperature {

    private double day;

    private double min;

    private double max;

    private double night;

    private double eve;

    private double morn;
}
