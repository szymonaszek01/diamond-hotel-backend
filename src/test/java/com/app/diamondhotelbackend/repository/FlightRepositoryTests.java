package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Flight;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
public class FlightRepositoryTests {

    @Autowired
    private FlightRepository flightRepository;

    private Flight flight;

    private List<Flight> flightList;

    @BeforeEach
    public void init() {
        flight = Flight.builder()
                .flightNumber("flightNumber1")
                .build();

        flightList = List.of(
                Flight.builder()
                        .flightNumber("flightNumber1")
                        .build(),
                Flight.builder()
                        .flightNumber("flightNumber2")
                        .build()
        );
    }

    @Test
    public void FlightRepository_Save_ReturnsSavedFlight() {
        Flight savedFlight = flightRepository.save(flight);

        Assertions.assertThat(savedFlight).isNotNull();
        Assertions.assertThat(savedFlight.getId()).isGreaterThan(0);
    }

    @Test
    public void FlightRepository_FindAll_ReturnsFlightList() {
        flightRepository.saveAll(flightList);
        List<Flight> foundFlightList = flightRepository.findAll();

        Assertions.assertThat(foundFlightList).isNotNull();
        Assertions.assertThat(foundFlightList.size()).isEqualTo(2);
    }

    @Test
    public void FlightRepository_FindById_ReturnsOptionalFlight() {
        Flight savedFlight = flightRepository.save(flight);
        Optional<Flight> flightOptional = flightRepository.findById((flight.getId()));

        Assertions.assertThat(flightOptional).isPresent();
        Assertions.assertThat(flightOptional.get().getId()).isEqualTo(savedFlight.getId());
    }

    @Test
    public void FlightRepository_FindByFlightNumber_ReturnsOptionalFlight() {
        Flight savedFlight = flightRepository.save(flight);
        Optional<Flight> flightOptional = flightRepository.findByFlightNumber((flight.getFlightNumber()));

        Assertions.assertThat(flightOptional).isPresent();
        Assertions.assertThat(flightOptional.get().getFlightNumber()).isEqualTo(savedFlight.getFlightNumber());
    }

    @Test
    public void FlightRepository_Update_ReturnsFlight() {
        Flight savedFlight = flightRepository.save(flight);
        Optional<Flight> flightOptional = flightRepository.findById((savedFlight.getId()));

        Assertions.assertThat(flightOptional).isPresent();
        Assertions.assertThat(flightOptional.get().getId()).isEqualTo(savedFlight.getId());

        flightOptional.get().setFlightNumber("flightNumber3");
        Flight updatedFlight = flightRepository.save(flightOptional.get());

        Assertions.assertThat(updatedFlight).isNotNull();
        Assertions.assertThat(updatedFlight.getFlightNumber()).isEqualTo("flightNumber3");
    }

    @Test
    public void FlightRepository_Delete_ReturnsNothing() {
        Flight savedFlight = flightRepository.save(flight);
        flightRepository.deleteById(savedFlight.getId());
        Optional<Flight> FlightOptional = flightRepository.findById(flight.getId());

        Assertions.assertThat(FlightOptional).isEmpty();
    }
}
