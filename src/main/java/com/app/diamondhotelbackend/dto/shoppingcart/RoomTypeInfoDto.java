package com.app.diamondhotelbackend.dto.shoppingcart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeInfoDto {

    @JsonProperty("room_type_name")
    private String roomTypeName;

    @JsonProperty("selected_rooms")
    private int selectedRooms;
}
