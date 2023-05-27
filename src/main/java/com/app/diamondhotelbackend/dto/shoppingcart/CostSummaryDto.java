package com.app.diamondhotelbackend.dto.shoppingcart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CostSummaryDto {

    @JsonProperty("total_without_tax")
    private BigDecimal totalWithoutTax;

    @JsonProperty("car_rent")
    private BigDecimal carRent;

    @JsonProperty("car_pick_up")
    private BigDecimal carPickUp;

    private BigDecimal tax;
}
