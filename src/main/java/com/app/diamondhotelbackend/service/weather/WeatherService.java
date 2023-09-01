package com.app.diamondhotelbackend.service.weather;

import com.app.diamondhotelbackend.entity.Weather;

import java.io.IOException;
import java.util.List;

public interface WeatherService {

    List<Weather> getWeatherList() throws IOException;
}
