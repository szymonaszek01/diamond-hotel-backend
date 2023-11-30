package com.app.diamondhotelbackend.dto.statistics.request;

import lombok.Getter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;

@Getter
public class StatisticsRequestDto {

    private final Date min;

    private final Date max;

    private final int year;

    private final int month;

    private final int lastDayOfTheMonth;


    public StatisticsRequestDto(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month != 0 ? month : 1);
        this.min = Date.valueOf(yearMonth.getYear() + "-" + yearMonth.getMonthValue() + "-" + "01");
        this.max = Date.valueOf(yearMonth.getYear() + "-" + (month != 0 ? yearMonth.getMonthValue() : "12") + "-" + (month != 0 ? yearMonth.lengthOfMonth() : "31"));
        this.year = year;
        this.month = month;
        this.lastDayOfTheMonth = yearMonth.lengthOfMonth();
    }

    public long getDateInMillis(int day) {
        return Date.valueOf(LocalDate.of(year, month, day)).getTime();
    }
}
