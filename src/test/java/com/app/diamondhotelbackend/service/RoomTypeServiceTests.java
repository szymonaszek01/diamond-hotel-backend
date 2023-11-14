package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.common.FileResponseDto;
import com.app.diamondhotelbackend.entity.RoomType;
import com.app.diamondhotelbackend.repository.RoomTypeRepository;
import com.app.diamondhotelbackend.service.roomtype.RoomTypeServiceImpl;
import com.app.diamondhotelbackend.util.UrlUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoomTypeServiceTests {

    @InjectMocks
    private RoomTypeServiceImpl roomTypeService;

    @Mock
    private RoomTypeRepository roomTypeRepository;

    private RoomType roomType;

    private List<RoomType> roomTypeList;

    private List<String> roomTypeNameList;

    @BeforeEach
    public void init() {
        roomType = RoomType.builder()
                .id(1)
                .name("Deluxe Suite")
                .adults(2)
                .children(2)
                .pricePerHotelNight(BigDecimal.valueOf(350))
                .image(UrlUtil.toImageAsByteArrayMapper("https://publish.purewow.net/wp-content/uploads/sites/2/2019/08/grand-velas.jpeg?fit=1360%2C906"))
                .equipment(Arrays.asList("King size bed", "Sofa bed", "Personalized Climate Control", "Minibar", "Balcony"))
                .build();

        roomTypeList = List.of(
                RoomType.builder()
                        .id(1)
                        .name("Deluxe Suite")
                        .adults(2)
                        .children(0)
                        .pricePerHotelNight(BigDecimal.valueOf(350))
                        .image(UrlUtil.toImageAsByteArrayMapper("https://publish.purewow.net/wp-content/uploads/sites/2/2019/08/grand-velas.jpeg?fit=1360%2C906"))
                        .build(),
                RoomType.builder()
                        .id(2)
                        .name("Family Room")
                        .adults(2)
                        .children(2)
                        .pricePerHotelNight(BigDecimal.valueOf(200))
                        .image(UrlUtil.toImageAsByteArrayMapper("https://images.unsplash.com/photo-1578683010236-d716f9a3f461?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8bHV4dXJ5JTIwYmVkcm9vbXxlbnwwfHwwfHw%3D&w=1000&q=80"))
                        .build()
        );

        roomTypeNameList = List.of("Deluxe Suite", "Family Room");
    }

    @Test
    public void RoomTypeService_CreateRoomType_ReturnsRoomType() {
        when(roomTypeRepository.save(Mockito.any(RoomType.class))).thenReturn(roomType);

        RoomType savedRoomType = roomTypeService.createRoomType(roomType);

        Assertions.assertThat(savedRoomType).isNotNull();
        Assertions.assertThat(savedRoomType.getName()).isEqualTo(roomType.getName());
    }

    @Test
    public void RoomTypeService_GetRoomTypeById_ReturnsRoomType() {
        when(roomTypeRepository.findById(Mockito.any(long.class))).thenReturn(Optional.of(roomType));

        RoomType foundRoomType = roomTypeService.getRoomTypeById(roomType.getId());

        Assertions.assertThat(foundRoomType).isNotNull();
        Assertions.assertThat(foundRoomType.getName()).isEqualTo(roomType.getName());
    }

    @Test
    public void RoomTypeService_GetRoomTypeByName_ReturnsRoomType() {
        when(roomTypeRepository.findByName(Mockito.any(String.class))).thenReturn(Optional.of(roomType));

        RoomType foundRoomType = roomTypeService.getRoomTypeByName(roomType.getName());

        Assertions.assertThat(foundRoomType).isNotNull();
        Assertions.assertThat(foundRoomType.getName()).isEqualTo(roomType.getName());

    }

    @Test
    public void RoomTypeService_GetRoomTypeList_ReturnsRoomTypeList() {
        when(roomTypeRepository.findAll()).thenReturn(roomTypeList);

        List<RoomType> foundRoomTypeList = roomTypeService.getRoomTypeList();

        Assertions.assertThat(foundRoomTypeList).isNotNull();
        Assertions.assertThat(foundRoomTypeList.size()).isEqualTo(2);
    }

    @Test
    public void RoomTypeService_GetRoomTypeNameList_ReturnsStringList() {
        when(roomTypeRepository.findAllNames()).thenReturn(roomTypeNameList);

        List<String> foundRoomTypeNameList = roomTypeService.getRoomTypeNameList();

        Assertions.assertThat(foundRoomTypeNameList).isNotNull();
        Assertions.assertThat(foundRoomTypeNameList.size()).isEqualTo(2);
    }

    @Test
    public void RoomTypeService_GetRoomTypeIdList_ReturnsLongList() {
        when(roomTypeRepository.findAll()).thenReturn(roomTypeList);

        List<Long> foundRoomTypeIdList = roomTypeService.getRoomTypeIdList();

        Assertions.assertThat(foundRoomTypeIdList).isNotNull();
        Assertions.assertThat(foundRoomTypeIdList.size()).isEqualTo(2);
    }

    @Test
    public void RoomTypeService_GetRoomTypeEquipmentById_ReturnsStringList() {
        when(roomTypeRepository.findEquipmentById(Mockito.any(long.class))).thenReturn(roomType.getEquipment());

        List<String> foundEquipment = roomTypeService.getRoomTypeEquipmentById(roomType.getId());

        Assertions.assertThat(foundEquipment).isNotNull();
        Assertions.assertThat(foundEquipment.size()).isEqualTo(roomType.getEquipment().size());
    }

    @Test
    public void RoomTypeService_GetRoomTypeImageById_ReturnsFileResponseDto() {
        when(roomTypeRepository.findImageById(Mockito.any(long.class))).thenReturn(HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d"));

        FileResponseDto fileResponseDto = roomTypeService.getRoomTypeImageById(roomType.getId());

        Assertions.assertThat(fileResponseDto).isNotNull();
        Assertions.assertThat(fileResponseDto.getEncodedFile()).isEqualTo(Base64.getEncoder().encodeToString(HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d")));
    }

    @Test
    public void RoomTypeService_UpdateUserProfilePicture_FileResponseDto() {
        try {
            MockMultipartFile file = new MockMultipartFile("image", "test.txt", "text/plain", HexFormat.of().parseHex("a04fd020ea3a6910a2d808002b30309d"));

            FileResponseDto fileResponseDto = roomTypeService.createRoomTypeImage(file);

            Assertions.assertThat(fileResponseDto).isNotNull();
            Assertions.assertThat(fileResponseDto.getEncodedFile()).isEqualTo(Base64.getEncoder().encodeToString(file.getBytes()));
            Assertions.assertThat(fileResponseDto.getFile()).isEqualTo(file.getBytes());

        } catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
    }
}
