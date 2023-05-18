package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.RoomTypeOpinionSummaryDto;
import com.app.diamondhotelbackend.repository.RoomTypeOpinionRepository;
import com.app.diamondhotelbackend.util.Constant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class RoomTypeOpinionService {

    private final RoomTypeOpinionRepository roomTypeOpinionRepository;

    public RoomTypeOpinionSummaryDto getRoomTypeOpinionSummaryDto(String roomTypeName) {
        long amount = roomTypeOpinionRepository.countAllByRoomTypeName(roomTypeName);
        double rate = roomTypeOpinionRepository.findAverageRateByRoomTypeName(roomTypeName);
        String text = getTextRepresentedRate(rate);

        return RoomTypeOpinionSummaryDto.builder().amount(amount).rate(rate).text(text).build();
    }

    private String getTextRepresentedRate(double rate) {
        if (rate < 5) {
            return Constant.BAD;
        } else if (rate < 8) {
            return Constant.GOOD;
        } else {
            return Constant.EXCELLENT;
        }
    }
}
