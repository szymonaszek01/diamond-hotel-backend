package com.app.diamondhotelbackend.dto.roomtype;

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
public class RoomTypeOfferDto {

    @JsonProperty("room_type_list")
    private List<RoomTypeDto> roomTypeDtoList;
}
