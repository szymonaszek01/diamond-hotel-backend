package com.app.diamondhotelbackend.dto.statistics.response;

import com.app.diamondhotelbackend.dto.statistics.model.StatisticsData;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StatisticsResponseDto {

    private double avg;

    @JsonProperty("data")
    private List<StatisticsData> statisticsDataList;
}
