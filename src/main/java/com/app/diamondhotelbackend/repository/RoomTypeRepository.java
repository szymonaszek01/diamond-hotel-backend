package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    @Query("SELECT DISTINCT r.name FROM RoomType r")
    List<String> findAllNameList();

    @Query("SELECT DISTINCT CAST(r.capacity as string) FROM RoomType r")
    List<String> findAllCapacityList();
}
