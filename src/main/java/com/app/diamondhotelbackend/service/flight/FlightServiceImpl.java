package com.app.diamondhotelbackend.service.flight;

import com.app.diamondhotelbackend.entity.Flight;
import com.app.diamondhotelbackend.exception.FlightProcessingException;
import com.app.diamondhotelbackend.repository.FlightRepository;
import com.app.diamondhotelbackend.util.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    @Override
    public Flight createFlight(Flight flight) {
        return flightRepository.save(flight);
    }

    @Override
    public Flight getFlightByFlightNumber(String flightNumber) throws FlightProcessingException {
        return flightRepository.findByFlightNumber(flightNumber).orElseThrow(() -> new FlightProcessingException(ConstantUtil.FLIGHT_NOT_FOUND_EXCEPTION));
    }
}
