package com.app.diamondhotelbackend.dto.room.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomDetailsDto {

    private int number;

    private int floor;

    @JsonProperty("room_type_name")
    private String roomTypeName;

    @JsonProperty("room_type_shortcut")
    private String roomTypeShortcut;

    private boolean occupied;

    @JsonProperty("reservation_id")
    private long reservationId;

    @JsonProperty("check_in")
    private String checkIn;

    @JsonProperty("check_out")
    private String checkOut;

    private String email;
}
