package com.app.diamondhotelbackend.dto.room.response;

import com.app.diamondhotelbackend.dto.room.model.RoomAvailability;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class RoomAvailableResponseDto {

    @JsonProperty("check_in")
    private Date checkIn;

    @JsonProperty("check_out")
    private Date checkOut;

    @JsonProperty("room_available_list")
    private List<RoomAvailability> roomAvailabilityList;
}
