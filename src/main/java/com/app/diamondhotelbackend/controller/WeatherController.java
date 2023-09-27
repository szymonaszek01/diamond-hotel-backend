package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.weather.response.WeatherResponseDto;
import com.app.diamondhotelbackend.service.weather.WeatherServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RequestMapping("/api/v1/weather")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://diamond-hotel-frontend-react.vercel.app/"}, allowCredentials = "true")
public class WeatherController {

    private final WeatherServiceImpl weatherService;

    @GetMapping("/all")
    public ResponseEntity<WeatherResponseDto> getWeatherList() {
        try {
            return ResponseEntity.ok(weatherService.getWeatherList());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
