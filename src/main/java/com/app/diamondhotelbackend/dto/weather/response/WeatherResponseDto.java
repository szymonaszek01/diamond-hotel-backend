package com.app.diamondhotelbackend.dto.weather.response;

import com.app.diamondhotelbackend.dto.weather.model.DailyWeather;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WeatherResponseDto {

    @JsonProperty("weather_list")
    private List<DailyWeather> dailyWeatherList;
}
