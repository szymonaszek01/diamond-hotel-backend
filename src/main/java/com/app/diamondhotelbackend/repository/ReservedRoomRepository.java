package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.ReservedRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservedRoomRepository extends JpaRepository<ReservedRoom, Long>, JpaSpecificationExecutor<ReservedRoom> {
}
