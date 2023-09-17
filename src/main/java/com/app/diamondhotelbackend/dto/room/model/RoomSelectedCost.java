package com.app.diamondhotelbackend.dto.room.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class RoomSelectedCost {

    @JsonProperty("room_type_id")
    private long roomTypeId;

    private String name;

    private int rooms;

    private BigDecimal cost;
}
