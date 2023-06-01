package com.app.diamondhotelbackend.dto.reservation;

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
public class UserReservationNewResponseDto {

    private String status;

    @JsonProperty("transaction_code")
    private String transactionCode;

    @JsonProperty("reservation_cost")
    private BigDecimal reservationCost;
}
