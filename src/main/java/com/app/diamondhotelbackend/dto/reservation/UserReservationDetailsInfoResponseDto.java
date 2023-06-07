package com.app.diamondhotelbackend.dto.reservation;

import com.app.diamondhotelbackend.dto.room.RoomDto;
import com.app.diamondhotelbackend.dto.roomtype.RoomTypeDto;
import com.app.diamondhotelbackend.dto.transaction.TransactionDto;
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
public class UserReservationDetailsInfoResponseDto {

    @JsonProperty("check_in")
    private String checkIn;

    @JsonProperty("check_out")
    private String checkOut;

    @JsonProperty("room_cost")
    private BigDecimal roomCost;

    @JsonProperty("flight_number")
    private String flightNumber;

    private String email;

    @JsonProperty("room_type")
    private RoomTypeDto roomTypeDto;

    @JsonProperty("room")
    private RoomDto roomDto;

    @JsonProperty("transaction")
    private TransactionDto transactionDto;
}
