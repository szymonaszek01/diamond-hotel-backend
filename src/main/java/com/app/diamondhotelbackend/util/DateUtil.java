package com.app.diamondhotelbackend.util;

import java.sql.Date;
import java.util.Optional;

public class DateUtil {

    public static Optional<Date> parseDate(String dateAsString) {
        try {
            if (dateAsString == null || dateAsString.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(Date.valueOf(dateAsString));

        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static long getDifferenceBetweenTwoDates(Date firstDate, Date secondDate) {
        return Math.round((double) Math.abs(secondDate.getTime() - firstDate.getTime()) / (1000 * 60 * 60 * 24));
    }
}
