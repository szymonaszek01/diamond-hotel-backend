package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.weather.model.DailyWeather;
import com.app.diamondhotelbackend.dto.weather.response.WeatherResponseDto;
import com.app.diamondhotelbackend.security.jwt.JwtFilter;
import com.app.diamondhotelbackend.service.weather.WeatherServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = WeatherController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class WeatherControllerTests {

    @MockBean
    private WeatherServiceImpl weatherService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private MockMvc mockMvc;

    private WeatherResponseDto weatherResponseDto;

    private static final String url = "/api/v1/weather";

    @BeforeEach
    public void init() {
        weatherResponseDto = WeatherResponseDto.builder()
                .dailyWeatherList(List.of(
                                DailyWeather.builder()
                                        .id(1)
                                        .build(),
                                DailyWeather.builder()
                                        .id(2)
                                        .build()
                        )
                ).build();
    }

    @Test
    public void WeatherController_GetWeatherList_ReturnsWeatherResponseDto() throws Exception {
        when(weatherService.getWeatherList()).thenReturn(weatherResponseDto);

        MockHttpServletRequestBuilder request = get(url + "/all")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.weather_list[0].id").value(1));
    }
}
