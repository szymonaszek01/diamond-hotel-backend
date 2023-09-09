package com.app.diamondhotelbackend.service.roomtype;

import com.app.diamondhotelbackend.entity.RoomType;
import com.app.diamondhotelbackend.exception.RoomTypeProcessingException;

import java.util.List;

public interface RoomTypeService {

    RoomType getRoomTypeById(long id) throws RoomTypeProcessingException;

    List<RoomType> getRoomTypeList();

    List<String> getRoomTypeNameList();

    List<Long> getRoomTypeIdList();

    List<String> getRoomTypeEquipmentById(long id);
}
