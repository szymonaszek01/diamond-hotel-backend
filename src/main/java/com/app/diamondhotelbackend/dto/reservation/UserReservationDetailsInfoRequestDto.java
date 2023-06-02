package com.app.diamondhotelbackend.dto.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReservationDetailsInfoRequestDto {

    @JsonProperty("user_profile_id")
    private long userProfileId;

    @JsonProperty("reservation_id")
    private long reservationId;
}
