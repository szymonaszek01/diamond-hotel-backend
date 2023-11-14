package com.app.diamondhotelbackend.service.roomtype;

import com.app.diamondhotelbackend.dto.common.FileResponseDto;
import com.app.diamondhotelbackend.entity.RoomType;
import com.app.diamondhotelbackend.exception.RoomTypeProcessingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface RoomTypeService {

    RoomType createRoomType(RoomType roomType);

    RoomType getRoomTypeById(long id) throws RoomTypeProcessingException;

    RoomType getRoomTypeByName(String name) throws RoomTypeProcessingException;

    List<RoomType> getRoomTypeList();

    List<String> getRoomTypeNameList();

    List<Long> getRoomTypeIdList();

    List<String> getRoomTypeEquipmentById(long id);

    FileResponseDto getRoomTypeImageById(long id);

    FileResponseDto createRoomTypeImage(MultipartFile file) throws IOException;
}
