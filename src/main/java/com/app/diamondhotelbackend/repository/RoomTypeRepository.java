package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    @Query("SELECT DISTINCT r.name FROM RoomType r")
    List<String> findAllNameList();

    @Query("SELECT DISTINCT CAST(r.capacity as string) FROM RoomType r")
    List<String> findAllCapacityList();

    @Query("SELECT r.pricePerHotelNight FROM RoomType r WHERE r.name = :roomTypeName")
    Optional<BigDecimal> findPricePerHotelNightByRoomTypeName(String roomTypeName);

    @Query("SELECT r.capacity FROM RoomType r WHERE r.name = :roomTypeName")
    Optional<Integer> findCapacityByRoomTypeName(String roomTypeName);
}
