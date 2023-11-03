package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.Room;
import com.app.diamondhotelbackend.entity.RoomType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@DataJpaTest
public class RoomRepositoryTests {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private Room room;

    private List<Room> roomList;

    private List<Long> roomTypeIdList;

    private BigDecimal pricePerHotelNight;

    @BeforeEach
    public void init() {
        List<RoomType> savedRoomTypeList = List.of(
                testEntityManager.persistAndFlush(RoomType.builder()
                        .name("Deluxe Suite")
                        .adults(2)
                        .children(0)
                        .pricePerHotelNight(BigDecimal.valueOf(350))
                        .build()),
                testEntityManager.persistAndFlush(RoomType.builder()
                        .name("Family Room")
                        .adults(2)
                        .children(2)
                        .pricePerHotelNight(BigDecimal.valueOf(200))
                        .build()),
                testEntityManager.persistAndFlush(RoomType.builder()
                        .name("Standard Room")
                        .adults(2)
                        .children(0)
                        .pricePerHotelNight(BigDecimal.valueOf(120))
                        .build())
        );

        room = Room.builder()
                .number(101)
                .floor(1)
                .roomType(savedRoomTypeList.get(0))
                .build();

        roomList = List.of(
                Room.builder()
                        .number(101)
                        .floor(1)
                        .roomType(savedRoomTypeList.get(0))
                        .build(),
                Room.builder()
                        .number(102)
                        .floor(1)
                        .roomType(savedRoomTypeList.get(1))
                        .build(),
                Room.builder()
                        .number(302)
                        .floor(3)
                        .roomType(savedRoomTypeList.get(2))
                        .build()
        );

        roomTypeIdList = List.of(savedRoomTypeList.get(0).getId(), savedRoomTypeList.get(1).getId());

        pricePerHotelNight = BigDecimal.valueOf(200);
    }

    @Test
    public void RoomRepository_Save_ReturnsSavedRoom() {
        Room savedRoom = roomRepository.save(room);

        Assertions.assertThat(savedRoom).isNotNull();
        Assertions.assertThat(savedRoom.getId()).isGreaterThan(0);
    }

    @Test
    public void RoomRepository_FindAll_ReturnsRoomList() {
        roomRepository.saveAll(roomList);
        List<Room> foundRoomList = roomRepository.findAll();

        Assertions.assertThat(foundRoomList).isNotNull();
        Assertions.assertThat(foundRoomList.size()).isEqualTo(3);
    }


    @Test
    public void RoomRepository_FindAllByRoomTypeIdIn_ReturnsRoomList() {
        roomRepository.saveAll(roomList);
        List<Room> foundRoomList = roomRepository.findAllByRoomTypeIdIn(roomTypeIdList);

        Assertions.assertThat(foundRoomList).isNotNull();
        Assertions.assertThat(foundRoomList.get(0).getRoomType().getId()).isEqualTo(roomTypeIdList.get(0));
        Assertions.assertThat(foundRoomList.get(1).getRoomType().getId()).isEqualTo(roomTypeIdList.get(1));
        Assertions.assertThat(foundRoomList.size()).isEqualTo(2);
    }

    @Test
    public void RoomRepository_FindAllByRoomTypePricePerHotelNightLessThanEqual_ReturnsRoomList() {
        roomRepository.saveAll(roomList);
        List<Room> foundRoomList = roomRepository.findAllByRoomTypePricePerHotelNightLessThanEqual(pricePerHotelNight);

        Assertions.assertThat(foundRoomList).isNotNull();
        Assertions.assertThat(foundRoomList.get(0).getRoomType().getPricePerHotelNight()).isLessThanOrEqualTo(pricePerHotelNight);
        Assertions.assertThat(foundRoomList.get(1).getRoomType().getPricePerHotelNight()).isLessThanOrEqualTo(pricePerHotelNight);
        Assertions.assertThat(foundRoomList.size()).isEqualTo(2);
    }

    @Test
    public void RoomRepository_FindAllByRoomTypeIdInAndRoomTypePricePerHotelNightLessThanEqual_ReturnsRoomList() {
        roomRepository.saveAll(roomList);
        List<Room> foundRoomList = roomRepository.findAllByRoomTypeIdInAndRoomTypePricePerHotelNightLessThanEqual(roomTypeIdList, pricePerHotelNight);

        Assertions.assertThat(foundRoomList).isNotNull();
        Assertions.assertThat(foundRoomList.get(0).getRoomType().getId()).isEqualTo(roomTypeIdList.get(1));
        Assertions.assertThat(foundRoomList.get(0).getRoomType().getPricePerHotelNight()).isLessThanOrEqualTo(pricePerHotelNight);
        Assertions.assertThat(foundRoomList.size()).isEqualTo(1);
    }

    @Test
    public void RoomRepository_FindById_ReturnsOptionalRoom() {
        Room savedRoom = roomRepository.save(room);
        Optional<Room> roomOptional = roomRepository.findById((room.getId()));

        Assertions.assertThat(roomOptional).isPresent();
        Assertions.assertThat(roomOptional.get().getId()).isEqualTo(savedRoom.getId());
    }

    @Test
    public void RoomRepository_Update_ReturnsRoom() {
        Room savedRoom = roomRepository.save(room);
        Optional<Room> roomOptional = roomRepository.findById((savedRoom.getId()));

        Assertions.assertThat(roomOptional).isPresent();
        Assertions.assertThat(roomOptional.get().getId()).isEqualTo(savedRoom.getId());

        roomOptional.get().setNumber(900);
        Room updatedRoom = roomRepository.save(roomOptional.get());

        Assertions.assertThat(updatedRoom).isNotNull();
        Assertions.assertThat(updatedRoom.getNumber()).isEqualTo(900);
    }

    @Test
    public void RoomRepository_Delete_ReturnsNothing() {
        Room savedRoom = roomRepository.save(room);
        roomRepository.deleteById(savedRoom.getId());
        Optional<Room> roomOptional = roomRepository.findById(room.getId());

        Assertions.assertThat(roomOptional).isEmpty();
    }
}
