package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.RoomTypeOpinion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomTypeOpinionRepository extends JpaRepository<RoomTypeOpinion, Long> {

    long countAllByRoomTypeName(String roomTypeName);

    @Query("SELECT AVG(r.rate) FROM RoomTypeOpinion r WHERE r.roomType.name = :roomTypeName")
    double findAverageRateByRoomTypeName(String roomTypeName);
}
