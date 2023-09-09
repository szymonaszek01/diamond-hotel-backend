package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.RoomTypeOpinion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeOpinionRepository extends JpaRepository<RoomTypeOpinion, Long> {
}
