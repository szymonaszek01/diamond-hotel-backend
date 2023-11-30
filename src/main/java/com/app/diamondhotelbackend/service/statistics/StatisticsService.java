package com.app.diamondhotelbackend.service.statistics;

import com.app.diamondhotelbackend.dto.statistics.request.StatisticsRequestDto;
import com.app.diamondhotelbackend.dto.statistics.response.StatisticsResponseDto;

import java.util.List;

public interface StatisticsService {

    List<Integer> getYearStatistics();

    List<String> getMonthStatistics();

    StatisticsResponseDto getSummaryStatistics(StatisticsRequestDto statisticsRequestDto);

    StatisticsResponseDto getRoomTypeStatistics(StatisticsRequestDto statisticsRequestDto);

    StatisticsResponseDto getUserProfileStatistics(StatisticsRequestDto statisticsRequestDto);

    StatisticsResponseDto getReservationStatistics(StatisticsRequestDto statisticsRequestDto);

    StatisticsResponseDto getReservedRoomStatistics(StatisticsRequestDto statisticsRequestDto);

    StatisticsResponseDto getIncomeStatistics(StatisticsRequestDto statisticsRequestDto);
}
