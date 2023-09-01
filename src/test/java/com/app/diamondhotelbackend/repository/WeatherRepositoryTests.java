package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Weather;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
public class WeatherRepositoryTests {

    @Autowired
    private WeatherRepository weatherRepository;

    private Weather weather;

    private List<Weather> weatherList;

    @BeforeEach
    public void init() {
        weather = Weather.builder()
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
                .build();

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
    public void WeatherRepository_Save_ReturnSavedUserProfile() {
        Weather savedWeather = weatherRepository.save(weather);

        Assertions.assertThat(savedWeather).isNotNull();
        Assertions.assertThat(savedWeather.getId()).isGreaterThan(0);
    }

    @Test
    public void WeatherRepository_FindAll_ReturnMoreThenOneUserProfile() {
        weatherRepository.saveAll(weatherList);
        List<Weather> foundWeatherList = weatherRepository.findAll();

        Assertions.assertThat(foundWeatherList).isNotNull();
        Assertions.assertThat(foundWeatherList.size()).isEqualTo(2);
    }

    @Test
    public void WeatherRepository_FindAllByOrderByDayAsc_ReturnUserProfile() {
        weatherRepository.saveAll(weatherList);
        List<Weather> foundWeatherList = weatherRepository.findAllByOrderByMeasuredAtAsc();
        Optional<Date> optionalMinDate = foundWeatherList.stream()
                .map(Weather::getMeasuredAt)
                .toList()
                .stream()
                .min(Comparator.naturalOrder());


        Assertions.assertThat(optionalMinDate).isPresent();
        Assertions.assertThat(foundWeatherList).isNotNull();
        Assertions.assertThat(foundWeatherList.get(0).getMeasuredAt()).isEqualTo(optionalMinDate.get());
        Assertions.assertThat(foundWeatherList.size()).isEqualTo(2);
    }

    @Test
    public void WeatherRepository_FindById_ReturnUserProfile() {
        Weather savedWeather = weatherRepository.save(weather);
        Optional<Weather> weatherOptional = weatherRepository.findById((savedWeather.getId()));

        Assertions.assertThat(weatherOptional).isPresent();
        Assertions.assertThat(weatherOptional.get().getId()).isEqualTo(savedWeather.getId());
    }

    @Test
    public void WeatherRepository_Update_ReturnsWeather() {
        Weather savedWeather = weatherRepository.save(weather);
        Optional<Weather> weatherOptional = weatherRepository.findById((savedWeather.getId()));

        Assertions.assertThat(weatherOptional).isPresent();
        Assertions.assertThat(weatherOptional.get().getId()).isEqualTo(savedWeather.getId());

        weatherOptional.get().setTemperatureDay(32);
        weatherOptional.get().setTemperatureNight(28);
        Weather updatedWeather = weatherRepository.save(weatherOptional.get());

        Assertions.assertThat(updatedWeather.getTemperatureDay()).isEqualTo(32);
        Assertions.assertThat(updatedWeather.getTemperatureNight()).isEqualTo(28);
    }

    @Test
    public void WeatherRepository_Delete_ReturnsNothing() {
        Weather savedWeather = weatherRepository.save(weather);
        weatherRepository.deleteById(savedWeather.getId());
        Optional<Weather> weatherOptional = weatherRepository.findById(weather.getId());

        Assertions.assertThat(weatherOptional).isEmpty();
    }
}
