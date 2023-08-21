package com.app.diamondhotelbackend.service.flight;

import com.app.diamondhotelbackend.entity.Flight;

public interface FlightService {

    Flight getOrCreateFlightByFlightNumber(String flightNumber);
}
