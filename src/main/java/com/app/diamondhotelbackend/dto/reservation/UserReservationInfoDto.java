package com.app.diamondhotelbackend.dto.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReservationInfoDto {

    private long id;

    @JsonProperty("transaction_code")
    private String transactionCode;

    @JsonProperty("room_type_name")
    private String roomType;

    @JsonProperty("check_in")
    private String checkIn;

    @JsonProperty("check_out")
    private String checkOut;

    private int capacity;

    @JsonProperty("room_number")
    private int roomNumber;

    @JsonProperty("room_floor")
    private int roomFloor;

    private String email;
}
