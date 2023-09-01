package com.app.diamondhotelbackend.service.weather;

import com.app.diamondhotelbackend.dto.openweather.model.WeatherDetails;
import com.app.diamondhotelbackend.dto.openweather.response.OpenWeatherResponseDto;
import com.app.diamondhotelbackend.dto.weather.model.DailyWeather;
import com.app.diamondhotelbackend.dto.weather.model.Measurement;
import com.app.diamondhotelbackend.dto.weather.response.WeatherResponseDto;
import com.app.diamondhotelbackend.entity.Weather;
import com.app.diamondhotelbackend.repository.WeatherRepository;
import com.app.diamondhotelbackend.util.ApplicationPropertiesUtil;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    private final WeatherRepository weatherRepository;

    private final ApplicationPropertiesUtil applicationPropertiesUtil;

    @Override
    public WeatherResponseDto getWeatherList() throws IOException {
        List<Weather> weatherList = weatherRepository.findAllByOrderByMeasuredAtAsc();
        if (weatherList.isEmpty() || isWeatherOutOfDate(weatherList)) {
            OpenWeatherResponseDto openWeatherResponseDto = getOpenWeatherResponseDto();
            weatherList = mapOpenWeatherResponseDtoToWeatherList(openWeatherResponseDto);
            weatherRepository.saveAll(weatherList);
        }

        return mapWeatherListToWeatherResponseDto(weatherList);
    }

    private boolean isWeatherOutOfDate(List<Weather> weatherList) {
        return weatherList.stream().anyMatch(weather -> {
            LocalDate now = LocalDate.now();
            LocalDate createdAt = weather.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            return now.getDayOfYear() > createdAt.getDayOfYear();
        });
    }

    private WeatherResponseDto mapWeatherListToWeatherResponseDto(List<Weather> weatherList) {
        return WeatherResponseDto.builder()
                .dailyWeatherList(weatherList.stream().map(this::mapWeatherToDailyWeather).collect(Collectors.toList()))
                .build();
    }

    private DailyWeather mapWeatherToDailyWeather(Weather weather) {
        return DailyWeather.builder()
                .id(weather.getId())
                .measuredAt(weather.getMeasuredAt())
                .main(weather.getMain())
                .description(weather.getDescription())
                .icon(weather.getIcon())
                .measurementList(
                        List.of(
                                Measurement.builder()
                                        .name("temperatureDay")
                                        .label("Temperature during the day")
                                        .unit("°")
                                        .value(weather.getTemperatureDay())
                                        .build(),
                                Measurement.builder()
                                        .name("temperatureNight")
                                        .label("Temperature at night")
                                        .unit("°")
                                        .value(weather.getTemperatureNight())
                                        .build(),
                                Measurement.builder()
                                        .name("pressure")
                                        .label("Pressure")
                                        .unit("hPa")
                                        .value(weather.getPressure())
                                        .build(),
                                Measurement.builder()
                                        .name("wind")
                                        .label("Wind")
                                        .unit("m/s")
                                        .value(weather.getWind())
                                        .build(),
                                Measurement.builder()
                                        .name("humidity")
                                        .label("Humidity")
                                        .unit("%")
                                        .value(weather.getHumidity())
                                        .build(),
                                Measurement.builder()
                                        .name("clouds")
                                        .label("Clouds")
                                        .unit("%")
                                        .value(weather.getClouds())
                                        .build(),
                                Measurement.builder()
                                        .name("rain")
                                        .label("Rain")
                                        .unit("mm")
                                        .value(weather.getRain())
                                        .build(),
                                Measurement.builder()
                                        .name("uvi")
                                        .label("UV")
                                        .unit("")
                                        .value(weather.getUvi())
                                        .build()
                        )
                )
                .build();
    }

    private List<Weather> mapOpenWeatherResponseDtoToWeatherList(OpenWeatherResponseDto openWeatherResponseDto) {
        return Arrays.stream(openWeatherResponseDto.getDailyList())
                .map(daily -> {
                    WeatherDetails weatherDetails = daily.getWeatherList().stream()
                            .findFirst()
                            .orElse(WeatherDetails.builder().build());

                    return Weather.builder()
                            .createdAt(new Date(System.currentTimeMillis()))
                            .measuredAt(new Date(daily.getDt() * 1000))
                            .main(weatherDetails.getMain())
                            .description(weatherDetails.getDescription())
                            .icon(ConstantUtil.OPEN_WEATHER_BASE_URI.replace("api.", "") + "/img/wn/" + weatherDetails.getIcon() + "@4x.png")
                            .temperatureDay(daily.getTemp().getDay())
                            .temperatureNight(daily.getTemp().getNight())
                            .pressure(daily.getPressure())
                            .wind(daily.getWindSpeed())
                            .humidity(daily.getHumidity())
                            .clouds(daily.getClouds())
                            .rain(daily.getRain())
                            .uvi(daily.getUvi())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private OpenWeatherResponseDto getOpenWeatherResponseDto() throws IOException {
        URL url = new URL(getUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder body = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            body.append(inputLine);
        }
        in.close();
        connection.disconnect();

        return new ObjectMapper().readValue(body.toString(), OpenWeatherResponseDto.class);
    }

    private String getUrl() {
        return UriComponentsBuilder.fromUriString(ConstantUtil.OPEN_WEATHER_BASE_URI + "/data/2.5/onecall")
                .queryParam(ConstantUtil.OPEN_WEATHER_ATTR_LAT, ConstantUtil.OPEN_WEATHER_VALUE_LAT)
                .queryParam(ConstantUtil.OPEN_WEATHER_ATTR_LON, ConstantUtil.OPEN_WEATHER_VALUE_LON)
                .queryParam(ConstantUtil.OPEN_WEATHER_ATTR_EXCLUDE, ConstantUtil.OPEN_WEATHER_VALUE_EXCLUDE)
                .queryParam(ConstantUtil.OPEN_WEATHER_ATTR_APPID, applicationPropertiesUtil.getOpenWeatherSecretKey())
                .queryParam(ConstantUtil.OPEN_WEATHER_ATTR_UNITS, ConstantUtil.OPEN_WEATHER_VALUE_UNITS)
                .build()
                .toUriString();
    }
}
