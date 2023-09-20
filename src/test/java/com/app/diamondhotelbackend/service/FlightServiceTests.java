package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.entity.Flight;
import com.app.diamondhotelbackend.repository.FlightRepository;
import com.app.diamondhotelbackend.service.flight.FlightServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FlightServiceTests {

    @InjectMocks
    private FlightServiceImpl flightService;

    @Mock
    private FlightRepository flightRepository;

    private Flight flight;

    @BeforeEach
    public void init() {
        flight = Flight.builder()
                .id(1)
                .flightNumber("flightNumber1")
                .build();
    }

    @Test
    public void FlightService_CreateFlight_ReturnsFlight() {
        when(flightRepository.save(Mockito.any(Flight.class))).thenReturn(flight);

        Flight savedFlight = flightService.createFlight(flight);

        Assertions.assertThat(savedFlight).isNotNull();
        Assertions.assertThat(savedFlight.getId()).isEqualTo(flight.getId());
    }

    @Test
    public void FlightService_GetFlightByFlightNumber_ReturnsFlight() {
        when(flightRepository.findByFlightNumber(Mockito.any(String.class))).thenReturn(Optional.of(flight));

        Flight savedFlight = flightService.getFlightByFlightNumber(flight.getFlightNumber());

        Assertions.assertThat(savedFlight).isNotNull();
        Assertions.assertThat(savedFlight.getFlightNumber()).isEqualTo(flight.getFlightNumber());
    }

    @Test
    public void FlightService_IsValidFlightNumber_ReturnsBoolean() {
        boolean isValidFlightNumber = flightService.isValidFlightNumber(flight.getFlightNumber());

        Assertions.assertThat(isValidFlightNumber).isNotNull();
        Assertions.assertThat(isValidFlightNumber).isEqualTo(false);
    }
}
