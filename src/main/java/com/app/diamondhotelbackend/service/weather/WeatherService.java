package com.app.diamondhotelbackend.service.weather;

import com.app.diamondhotelbackend.dto.weather.response.WeatherResponseDto;

import java.io.IOException;
import java.util.List;

public interface WeatherService {

    WeatherResponseDto getWeatherList() throws IOException;
}
