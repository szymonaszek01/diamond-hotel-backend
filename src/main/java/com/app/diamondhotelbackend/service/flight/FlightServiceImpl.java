package com.app.diamondhotelbackend.service.flight;

import com.app.diamondhotelbackend.entity.Flight;
import com.app.diamondhotelbackend.repository.FlightRepository;
import com.app.diamondhotelbackend.service.flight.FlightService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    @Override
    public Flight getOrCreateFlightByFlightNumber(String flightNumber) {
        Optional<Flight> flight = flightRepository.findFlightByFlightNumber(flightNumber);
        return flight.orElseGet(() -> flightRepository.save(Flight.builder().flightNumber(flightNumber).build()));
    }
}
