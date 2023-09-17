package com.app.diamondhotelbackend.dto.room.response;

import com.app.diamondhotelbackend.dto.room.model.RoomSelectedCost;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class RoomSelectedCostResponseDto {

    @JsonProperty("check_in")
    private Date checkIn;

    @JsonProperty("check_out")
    private Date checkOut;

    @JsonProperty("room_selected_cost")
    private RoomSelectedCost roomSelectedCost;
}
