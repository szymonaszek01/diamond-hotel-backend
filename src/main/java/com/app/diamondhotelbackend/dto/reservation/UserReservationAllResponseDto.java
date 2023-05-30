package com.app.diamondhotelbackend.dto.reservation;

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
public class UserReservationAllResponseDto {

    @JsonProperty("user_profile_id")
    private long userProfileId;

    @JsonProperty("reservation_list")
    private List<UserReservationInfoDto> userReservationInfoDtoList;
}
