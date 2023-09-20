package com.app.diamondhotelbackend.service.flight;

import com.app.diamondhotelbackend.entity.Flight;
import com.app.diamondhotelbackend.exception.FlightProcessingException;

public interface FlightService {

    Flight createFlight(Flight flight);

    Flight getFlightByFlightNumber(String flightNumber) throws FlightProcessingException;

    boolean isValidFlightNumber(String flightNumber);
}
