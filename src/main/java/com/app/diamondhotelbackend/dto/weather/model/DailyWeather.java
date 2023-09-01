package com.app.diamondhotelbackend.dto.weather.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class DailyWeather {

    private long id;

    @JsonProperty("measured_at")
    private Date measuredAt;

    private String main;

    private String description;

    private String icon;

    @JsonProperty("measurement_list")
    private List<Measurement> measurementList;
}
