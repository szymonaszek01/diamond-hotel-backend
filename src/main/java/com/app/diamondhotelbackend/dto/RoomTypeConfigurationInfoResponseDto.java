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
public class RoomTypeConfigurationInfoResponseDto {

    @JsonProperty("room_type_list")
    private List<String> roomTypeList;

    @JsonProperty("capacity_list")
    private List<String> capacityList;
}
