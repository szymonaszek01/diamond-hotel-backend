package com.app.diamondhotelbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableRoomDto {

    @JsonProperty("room_type_id")
    private long roomTypeId;

    @JsonProperty("available_rooms")
    private int availableRooms;
}
