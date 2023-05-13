package com.app.diamondhotelbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableRoomsRequestDto {

    @JsonProperty("check_in")
    private String checkIn;

    @JsonProperty("check_out")
    private String checkOut;

    @JsonProperty("room_type_id")
    private long roomTypeId;

    @JsonProperty("number_of_people")
    private long numberOfPeople;
}
