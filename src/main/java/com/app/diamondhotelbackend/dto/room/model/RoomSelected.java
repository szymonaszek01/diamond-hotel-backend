package com.app.diamondhotelbackend.dto.room.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomSelected {

    @JsonProperty("room_type_id")
    private long roomTypeId;

    private int rooms;
}
