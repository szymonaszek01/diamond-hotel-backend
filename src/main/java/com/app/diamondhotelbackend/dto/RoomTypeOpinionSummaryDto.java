package com.app.diamondhotelbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeOpinionSummaryDto {

    @JsonProperty("opinion_amount")
    private long amount;

    @JsonProperty("opinion_summary_rate")
    private double rate;

    @JsonProperty("opinion_summary_text")
    private String text;
}
