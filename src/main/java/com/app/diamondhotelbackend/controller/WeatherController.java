package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.entity.Weather;
import com.app.diamondhotelbackend.service.weather.WeatherServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequestMapping("/api/v1/weather")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
public class WeatherController {

    private final WeatherServiceImpl weatherService;

    @GetMapping("/all")
    public List<Weather> getWeatherList() {
        return weatherService.getWeatherList();
    }
}
