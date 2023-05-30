package com.app.diamondhotelbackend.dto.reservation;

import com.app.diamondhotelbackend.dto.roomtype.AvailableRoomTypeListRequestDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReservationAllRequestDto extends AvailableRoomTypeListRequestDto {

    @JsonProperty("user_profile_id")
    private long userProfileId;
}
