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
public class UserReservationCancellationResponseDto {

    @JsonProperty("reservation_id")
    private long reservationId;

    private String status;
}
