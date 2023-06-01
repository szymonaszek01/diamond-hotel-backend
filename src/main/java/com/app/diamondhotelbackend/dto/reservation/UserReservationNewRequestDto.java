package com.app.diamondhotelbackend.dto.reservation;

import com.app.diamondhotelbackend.dto.shoppingcart.CostSummaryDto;
import com.app.diamondhotelbackend.dto.shoppingcart.RoomTypeInfoDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReservationNewRequestDto {

    @JsonProperty("user_profile_id")
    private long userProfileId;

    @JsonProperty("check_in")
    private String checkIn;

    @JsonProperty("check_out")
    private String checkOut;

    @JsonProperty("room_type_info")
    private List<RoomTypeInfoDto> roomTypeInfoDtoList;

    @JsonProperty("flight_number")
    private String flightNumber;

    private CostSummaryDto cost;
}
