package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.RoomType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
public class RoomTypeRepositoryTests {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    private RoomType roomType;

    private List<RoomType> roomTypeList;

    @BeforeEach
    public void init() {
        roomType = RoomType.builder()
                .name("Deluxe Suite")
                .adults(2)
                .children(2)
                .pricePerHotelNight(BigDecimal.valueOf(350))
                .image(HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d"))
                .build();

        roomTypeList = List.of(
                RoomType.builder()
                        .name("Deluxe Suite")
                        .adults(2)
                        .children(0)
                        .pricePerHotelNight(BigDecimal.valueOf(350))
                        .image(HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d"))
                        .equipment(Arrays.asList("King size bed", "Sofa bed", "Personalized Climate Control", "Minibar", "Balcony"))
                        .build(),
                RoomType.builder()
                        .name("Family Room")
                        .adults(2)
                        .children(2)
                        .pricePerHotelNight(BigDecimal.valueOf(200))
                        .image(HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d"))
                        .equipment(Arrays.asList("2 queen size beds", "Coffee maker", "Mini fridge", "Bathtub", "Personalized Climate Control"))
                        .build()
        );
    }

    @Test
    public void RoomTypeRepository_Save_ReturnsSavedRoomType() {
        RoomType savedRoomType = roomTypeRepository.save(roomType);

        Assertions.assertThat(savedRoomType).isNotNull();
        Assertions.assertThat(savedRoomType.getId()).isGreaterThan(0);
    }

    @Test
    public void RoomTypeRepository_FindAll_ReturnsRoomTypeList() {
        roomTypeRepository.saveAll(roomTypeList);
        List<RoomType> foundRoomTypeList = roomTypeRepository.findAll();

        Assertions.assertThat(foundRoomTypeList).isNotNull();
        Assertions.assertThat(foundRoomTypeList.size()).isEqualTo(2);
    }

    @Test
    public void RoomTypeRepository_FindAllNames_ReturnsStringList() {
        roomTypeRepository.saveAll(roomTypeList);
        List<String> foundRoomTypeNameList = roomTypeRepository.findAllNames();

        Assertions.assertThat(foundRoomTypeNameList).isNotNull();
        Assertions.assertThat(foundRoomTypeNameList.get(0)).isEqualTo(roomTypeList.get(0).getName());
        Assertions.assertThat(foundRoomTypeNameList.size()).isEqualTo(2);
    }

    @Test
    public void RoomTypeRepository_FindById_ReturnsOptionalRoomType() {
        RoomType savedRoomType = roomTypeRepository.save(roomType);
        Optional<RoomType> roomTypeOptional = roomTypeRepository.findById((roomType.getId()));

        Assertions.assertThat(roomTypeOptional).isPresent();
        Assertions.assertThat(roomTypeOptional.get().getId()).isEqualTo(savedRoomType.getId());
    }

    @Test
    public void RoomTypeRepository_FindByName_ReturnsOptionalRoomType() {
        RoomType savedRoomType = roomTypeRepository.save(roomType);
        Optional<RoomType> roomTypeOptional = roomTypeRepository.findByName((roomType.getName()));

        Assertions.assertThat(roomTypeOptional).isPresent();
        Assertions.assertThat(roomTypeOptional.get().getId()).isEqualTo(savedRoomType.getId());
        Assertions.assertThat(roomTypeOptional.get().getName()).isEqualTo(savedRoomType.getName());
    }

    @Test
    public void RoomTypeRepository_FindEquipmentById_ReturnsStringList() {
        List<RoomType> savedRoomTypeList = roomTypeRepository.saveAll(roomTypeList);

        Assertions.assertThat(savedRoomTypeList).isNotNull();
        Assertions.assertThat(savedRoomTypeList.size()).isEqualTo(2);

        List<String> savedEquipment = roomTypeRepository.findEquipmentById(savedRoomTypeList.get(0).getId());

        Assertions.assertThat(savedEquipment).isNotNull();
        Assertions.assertThat(savedEquipment.size()).isEqualTo(savedRoomTypeList.get(0).getEquipment().size());
    }

    @Test
    public void RoomTypeRepository_Update_ReturnsRoomType() {
        RoomType savedRoomType = roomTypeRepository.save(roomType);
        Optional<RoomType> roomTypeOptional = roomTypeRepository.findById((savedRoomType.getId()));

        Assertions.assertThat(roomTypeOptional).isPresent();
        Assertions.assertThat(roomTypeOptional.get().getId()).isEqualTo(savedRoomType.getId());

        roomTypeOptional.get().setName("Perfect room");
        RoomType updatedRoomType = roomTypeRepository.save(roomTypeOptional.get());

        Assertions.assertThat(updatedRoomType).isNotNull();
        Assertions.assertThat(updatedRoomType.getName()).isEqualTo("Perfect room");
    }

    @Test
    public void RoomTypeRepository_Delete_ReturnsNothing() {
        RoomType savedRoomType = roomTypeRepository.save(roomType);
        roomTypeRepository.deleteById(savedRoomType.getId());
        Optional<RoomType> roomTypeOptional = roomTypeRepository.findById(roomType.getId());

        Assertions.assertThat(roomTypeOptional).isEmpty();
    }

    @Test
    public void RoomTypeRepository_FindImageById_ReturnsByteArray() {
        RoomType savedRoomType = roomTypeRepository.save(roomType);

        byte[] image = roomTypeRepository.findImageById(savedRoomType.getId());

        Assertions.assertThat(image).isNotNull();
    }
}
