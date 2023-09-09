package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findAllByRoomTypeIdIn(List<Long> roomTypeIdList);

    List<Room> findAllByRoomTypePricePerHotelNightLessThanEqual(BigDecimal pricePerHotelNight);

    List<Room> findAllByRoomTypeIdInAndRoomTypePricePerHotelNightLessThanEqual(List<Long> roomTypeIdList, BigDecimal pricePerHotelNight);
}
