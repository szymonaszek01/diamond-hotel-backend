package com.app.diamondhotelbackend.dto.transaction;

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
public class TransactionDto {

    private String code;

    @JsonProperty("total_without_tax")
    private BigDecimal totalWithoutTax;

    private BigDecimal tax;

    @JsonProperty("car_rent")
    private BigDecimal carRent;

    @JsonProperty("car_pick_up")
    private BigDecimal carPickUp;

    private String status;
}
