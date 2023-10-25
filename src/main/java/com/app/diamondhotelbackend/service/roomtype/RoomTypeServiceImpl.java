package com.app.diamondhotelbackend.service.roomtype;

import com.app.diamondhotelbackend.entity.RoomType;
import com.app.diamondhotelbackend.exception.RoomTypeProcessingException;
import com.app.diamondhotelbackend.repository.RoomTypeRepository;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.UrlUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    @Override
    public RoomType getRoomTypeById(long id) throws RoomTypeProcessingException {
        return roomTypeRepository.findById(id).orElseThrow(() -> new RoomTypeProcessingException(ConstantUtil.ROOM_TYPE_NOT_FOUND_EXCEPTION));
    }

    @Override
    public RoomType getRoomTypeByName(String name) throws RoomTypeProcessingException {
        return roomTypeRepository.findByName(UrlUtil.decode(name)).orElseThrow(() -> new RoomTypeProcessingException(ConstantUtil.ROOM_TYPE_NOT_FOUND_EXCEPTION));
    }

    @Override
    public List<RoomType> getRoomTypeList() {
        return roomTypeRepository.findAll();
    }

    @Override
    public List<String> getRoomTypeNameList() {
        return roomTypeRepository.findAllNames();
    }

    @Override
    public List<Long> getRoomTypeIdList() {
        return roomTypeRepository.findAll().stream().map(RoomType::getId).toList();
    }

    @Override
    public List<String> getRoomTypeEquipmentById(long id) {
        return roomTypeRepository.findEquipmentById(id);
    }
}
