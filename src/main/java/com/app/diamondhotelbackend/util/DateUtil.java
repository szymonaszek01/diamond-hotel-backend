package com.app.diamondhotelbackend.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DateUtil {

    public static Optional<LocalDateTime> isValidCheckInOrCheckOut(String localDateTimeAsString) {
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

    public static long getDuration(LocalDateTime checkIn, LocalDateTime checkOut) {
        return Duration.between(checkIn, checkOut).toDays();
    }
}
