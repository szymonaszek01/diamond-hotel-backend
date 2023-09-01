package com.app.diamondhotelbackend.dto.openweather.response;

import com.app.diamondhotelbackend.dto.openweather.model.CurrentWeather;
import com.app.diamondhotelbackend.dto.openweather.model.DailyWeather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OpenWeatherResponseDto {

    private double lat;

    private double lon;

    private String timezone;

    @JsonProperty("timezone_offset")
    private int timezoneOffset;

    @JsonProperty("current")
    private CurrentWeather currentWeather;

    @JsonProperty("daily")
    private DailyWeather[] dailyList;
}
