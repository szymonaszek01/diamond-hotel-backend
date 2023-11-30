package com.app.diamondhotelbackend.util;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

    public static String getMonth(Date date) {
        LocalDate localDate = date.toLocalDate();
        return UrlUtil.capitalize(localDate.getMonth().toString());
    }

    public static Date toSqlDateMapper(java.util.Date date) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return new Date(dateFormat.parse(dateFormat.format(date)).getTime());
        } catch (ParseException e) {
            return new Date(System.currentTimeMillis());
        }
    }

    public static Date toUtilDateMapper(java.sql.Date date, boolean isEndOfTheDay) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return new Date(dateFormat.parse(date.toString() + (isEndOfTheDay ? " 23:59:59" : " 00:00:00")).getTime());
        } catch (ParseException e) {
            return new Date(System.currentTimeMillis());
        }
    }

    public static boolean isDateBetween(long dateInMillis, Date min, Date max) {
        return min.getTime() <= dateInMillis && max.getTime() >= dateInMillis;
    }

    public static boolean isDateEqual(long dateInMillis, Date date) {
        return date.getTime() == dateInMillis;
    }

    public static boolean isMonthEqual(String month, Date min, Date max) {
        return month.equals(getMonth(min)) || month.equals(getMonth(max));
    }

    public static boolean isMonthEqual(String month, Date date) {
        return month.equals(getMonth(date));
    }
}
