package com.app.diamondhotelbackend.dto.openweather.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DailyWeather {

    private long dt;

    private long sunrise;

    private long sunset;

    private long moonrise;

    private long moonset;

    @JsonProperty("moon_phase")
    private double moonPhase;

    private Temperature temp;

    @JsonProperty("feels_like")
    private FeelsLike feelsLike;

    private int pressure;

    private int humidity;

    @JsonProperty("dew_point")
    private double dewPoint;

    @JsonProperty("wind_speed")
    private double windSpeed;

    @JsonProperty("wind_deg")
    private int windDeg;

    @JsonProperty("wind_gust")
    private double windGust;

    @JsonProperty("weather")
    private List<WeatherDetails> weatherList;

    private int clouds;

    private double pop;

    private double rain;

    private double uvi;
}
