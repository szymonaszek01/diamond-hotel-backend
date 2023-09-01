package com.app.diamondhotelbackend.dto.weather.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Measurement {

    private String name;

    private String label;

    private String unit;

    private double value;
}
