package com.app.diamondhotelbackend.dto.reservation.request;

import com.app.diamondhotelbackend.dto.room.model.RoomSelected;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReservationCreateRequestDto {

    @JsonProperty("user_profile_id")
    private long userProfileId;

    @JsonProperty("check_in")
    private String checkIn;

    @JsonProperty("check_out")
    private String checkOut;

    private int adults;

    private int children;

    @JsonProperty("flight_number")
    private String flightNumber;

    @JsonProperty("room_selected_list")
    private List<RoomSelected> roomSelectedList;
}
