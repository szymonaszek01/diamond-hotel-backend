package com.app.diamondhotelbackend.dto.shoppingcart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingCartSummaryResponseDto {

    @JsonProperty("check_in")
    private String checkIn;

    @JsonProperty("check_out")
    private String checkOut;

    @JsonProperty("cost_summary")
    private CostSummaryDto costSummaryDto;

    @JsonProperty("room_type_summary")
    private List<RoomTypeSummaryDto> roomTypeSummary;
}
