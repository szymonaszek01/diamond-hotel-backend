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
public class CarDto {

    @JsonProperty("check_in")
    private String checkIn;

    @JsonProperty("check_out")
    private String checkOut;

    @JsonProperty("total_room_cost")
    private BigDecimal totalRoomCost;

    @JsonProperty("car_rent")
    private boolean carRent;

    @JsonProperty("car_rent_duration")
    private int carRentDuration;

    @JsonProperty("car_pick_up")
    private boolean carPickUp;
}
