package com.app.diamondhotelbackend.dto.openweather.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CurrentWeather {

    private long dt;

    private long sunrise;

    private long sunset;

    private double temp;

    @JsonProperty("feels_like")
    private double feelsLike;

    private int pressure;

    private int humidity;

    @JsonProperty("dew_point")
    private double dewPoint;

    private double uvi;

    private int clouds;

    private int visibility;

    @JsonProperty("wind_speed")
    private double windSpeed;

    @JsonProperty("wind_deg")
    private int windDeg;

    @JsonProperty("wind_gust")
    private double windGust;

    @JsonProperty("weather")
    private List<WeatherDetails> weatherList;

    private Rain rain;
}
