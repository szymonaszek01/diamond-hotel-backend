package com.app.diamondhotelbackend.dto;

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
public class AvailableRoomTypeListResponseDto {

    @JsonProperty("available_room_type_list")
    private List<AvailableRoomTypeDto> availableRoomDtoList;
}
