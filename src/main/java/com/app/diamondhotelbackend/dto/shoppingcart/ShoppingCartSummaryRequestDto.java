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
public class ShoppingCartSummaryRequestDto {

    @JsonProperty("check_in")
    private String checkIn;

    @JsonProperty("check_out")
    private String checkOut;

    @JsonProperty("flight_number")
    private String flightNumber;

    @JsonProperty("car_pick_up_option")
    private boolean carPickUp;

    @JsonProperty("car_rent_option")
    private boolean carRent;

    @JsonProperty("room_type_info")
    private List<RoomTypeInfoDto> roomTypeInfo;
}
