package com.app.diamondhotelbackend.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class DateService {

    public Optional<LocalDateTime> isValidCheckInOrCheckOut(String localDateTimeAsString) {
        try {
            if (localDateTimeAsString == null || localDateTimeAsString.isEmpty()) {
                return Optional.empty();
            }
            LocalDateTime localDateTime = LocalDateTime.parse(localDateTimeAsString, DateTimeFormatter.ISO_DATE_TIME);
            return Optional.of(localDateTime);

        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public long getDuration(LocalDateTime checkIn, LocalDateTime checkOut) {
        return Duration.between(checkIn, checkOut).toDays();
    }
}
