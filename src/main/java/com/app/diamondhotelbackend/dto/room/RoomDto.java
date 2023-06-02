package com.app.diamondhotelbackend.dto.room;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDto {

    @JsonProperty("room_number")
    private int roomNumber;

    private int floor;
}
