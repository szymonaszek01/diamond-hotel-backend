package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.weather.response.WeatherResponseDto;
import com.app.diamondhotelbackend.entity.Weather;
import com.app.diamondhotelbackend.repository.WeatherRepository;
import com.app.diamondhotelbackend.service.weather.WeatherServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTests {

    @InjectMocks
    private WeatherServiceImpl weatherService;

    @Mock
    private WeatherRepository weatherRepository;

    private List<Weather> weatherList;

    @BeforeEach
    public void init() {
        weatherList = List.of(
                Weather.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .measuredAt(new Date(System.currentTimeMillis()))
                        .main("Rain")
                        .description("moderate rain")
                        .icon("10d")
                        .temperatureDay(28.18)
                        .temperatureNight(28)
                        .pressure(1011)
                        .wind(8.49)
                        .humidity(78)
                        .build(),
                Weather.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .measuredAt(new Date(System.currentTimeMillis()))
                        .main("Rain")
                        .description("light rain")
                        .icon("10d")
                        .temperatureDay(26.68)
                        .temperatureNight(24)
                        .pressure(1012)
                        .wind(6.29)
                        .humidity(82)
                        .build()
        );
    }

    @Test
    public void WeatherService_GetWeatherList_ReturnsWeatherList() throws IOException {
        when(weatherRepository.findAllByOrderByMeasuredAtAsc()).thenReturn(weatherList);

        WeatherResponseDto createdWeatherResponseDto = weatherService.getWeatherList();

        Assertions.assertThat(createdWeatherResponseDto).isNotNull();
        Assertions.assertThat(createdWeatherResponseDto.getDailyWeatherList().size()).isEqualTo(2);
    }
}
