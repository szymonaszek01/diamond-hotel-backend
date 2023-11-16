package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findAllByRoomTypeIdIn(List<Long> roomTypeIdList);

    List<Room> findAllByRoomTypePricePerHotelNightLessThanEqual(BigDecimal pricePerHotelNight);

    List<Room> findAllByRoomTypeIdInAndRoomTypePricePerHotelNightLessThanEqual(List<Long> roomTypeIdList, BigDecimal pricePerHotelNight);

    Optional<Room> findByNumberAndFloor(int number, int floor);

    @Query("SELECT DISTINCT r.floor FROM Room r")
    List<Integer> findAllFloors();

    Page<Room> findAllByFloor(int floor, Pageable pageable);
}
