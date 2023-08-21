package com.app.diamondhotelbackend.service.roomtypeopinion;

import com.app.diamondhotelbackend.dto.roomtype.RoomTypeOpinionSummaryDto;

public interface RoomTypeOpinionService {

    RoomTypeOpinionSummaryDto getRoomTypeOpinionSummaryDto(String roomTypeName);
}
