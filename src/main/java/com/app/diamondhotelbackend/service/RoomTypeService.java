package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.RoomTypeConfigurationInfoResponseDto;
import com.app.diamondhotelbackend.repository.RoomTypeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    public RoomTypeConfigurationInfoResponseDto getRoomTypeConfigurationInfo() {
        List<String> roomTypeList = roomTypeRepository.findAllRoomTypeNameList();
        List<String> capacityList = roomTypeRepository.findAllRoomTypeCapacityList();

        return RoomTypeConfigurationInfoResponseDto.builder().roomTypeList(roomTypeList).capacityList(capacityList).build();
    }
}
