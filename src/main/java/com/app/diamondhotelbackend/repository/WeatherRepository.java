package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {

    List<Weather> findAllByOrderByMeasuredAtAsc();
}
