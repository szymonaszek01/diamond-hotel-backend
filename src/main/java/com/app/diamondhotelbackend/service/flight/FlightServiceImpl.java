package com.app.diamondhotelbackend.service.flight;

import com.app.diamondhotelbackend.entity.Flight;
import com.app.diamondhotelbackend.exception.FlightProcessingException;
import com.app.diamondhotelbackend.repository.FlightRepository;
import com.app.diamondhotelbackend.util.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Override
    public boolean isValidFlightNumber(String flightNumber) {
        if (flightNumber == null || flightNumber.isEmpty()) {
            return false;
        }

        String regex = "^[A-Z]{2}\\s{0,1}[0-9]{3,4}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(flightNumber);

        return matcher.matches();
    }
}
