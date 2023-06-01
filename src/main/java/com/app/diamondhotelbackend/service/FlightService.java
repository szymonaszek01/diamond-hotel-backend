package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.entity.Flight;
import com.app.diamondhotelbackend.repository.FlightRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class FlightService {

    private final FlightRepository flightRepository;

    public Flight getOrCreateFlightByFlightNumber(String flightNumber) {
        Optional<Flight> flight = flightRepository.findFlightByFlightNumber(flightNumber);
        return flight.orElseGet(() -> flightRepository.save(Flight.builder().flightNumber(flightNumber).build()));
    }
}
