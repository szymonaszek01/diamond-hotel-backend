package com.app.diamondhotelbackend.dto.room.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddRoomRequestDto {

    private int number;

    private int floor;

    @JsonProperty("room_type_id")
    private long roomTypeId;
}
