package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.common.FileResponseDto;
import com.app.diamondhotelbackend.entity.RoomType;
import com.app.diamondhotelbackend.security.jwt.JwtFilter;
import com.app.diamondhotelbackend.service.roomtype.RoomTypeServiceImpl;
import com.app.diamondhotelbackend.util.UrlUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = RoomTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class RoomTypeControllerTests {

    private static final String url = "/api/v1/room-type";
    @MockBean
    private RoomTypeServiceImpl roomTypeService;
    @MockBean
    private JwtFilter jwtFilter;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private RoomType roomType;
    private List<RoomType> roomTypeList;
    private List<String> roomTypeNameList;
    private FileResponseDto fileResponseDto;

    @BeforeEach
    public void init() {
        roomType = RoomType.builder()
                .id(1)
                .name("Deluxe Suite")
                .adults(2)
                .children(0)
                .pricePerHotelNight(BigDecimal.valueOf(350))
                .image(UrlUtil.toImageAsByteArrayMapper("https://publish.purewow.net/wp-content/uploads/sites/2/2019/08/grand-velas.jpeg?fit=1360%2C906"))
                .equipment(Arrays.asList("King size bed", "Sofa bed", "Personalized Climate Control", "Minibar", "Balcony"))
                .build();

        roomTypeList = List.of(
                RoomType.builder()
                        .name("Deluxe Suite")
                        .adults(2)
                        .children(0)
                        .pricePerHotelNight(BigDecimal.valueOf(350))
                        .image(UrlUtil.toImageAsByteArrayMapper("https://publish.purewow.net/wp-content/uploads/sites/2/2019/08/grand-velas.jpeg?fit=1360%2C906"))
                        .build(),
                RoomType.builder()
                        .name("Family Room")
                        .adults(2)
                        .children(2)
                        .pricePerHotelNight(BigDecimal.valueOf(200))
                        .image(UrlUtil.toImageAsByteArrayMapper("https://images.unsplash.com/photo-1578683010236-d716f9a3f461?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8bHV4dXJ5JTIwYmVkcm9vbXxlbnwwfHwwfHw%3D&w=1000&q=80"))
                        .build()
        );

        roomTypeNameList = List.of("Deluxe Suite", "Family Room");

        fileResponseDto = FileResponseDto.builder()
                .fileName("room-type-" + roomType.getId() + "-image")
                .encodedFile(Base64.getEncoder().encodeToString(HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d")))
                .build();
    }

    @Test
    public void RoomTypeController_CreateRoomType_ReturnsRoomType() throws Exception {
        when(roomTypeService.createRoomType(Mockito.any(RoomType.class))).thenReturn(roomType);

        MockHttpServletRequestBuilder request = post(url + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roomType));


        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(roomType.getName()));
    }

    @Test
    public void RoomTypeController_GetRoomTypeById_ReturnsRoomType() throws Exception {
        when(roomTypeService.getRoomTypeById(Mockito.any(long.class))).thenReturn(roomType);

        MockHttpServletRequestBuilder request = get(url + "/id/" + roomType.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(roomType.getName()));
    }

    @Test
    public void RoomTypeController_GetRoomTypeByName_ReturnsRoomType() throws Exception {
        when(roomTypeService.getRoomTypeByName(Mockito.any(String.class))).thenReturn(roomType);

        MockHttpServletRequestBuilder request = get(url + "/name/" + roomType.getName())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(roomType.getName()));
    }

    @Test
    public void RoomTypeController_GetRoomTypeList_ReturnsRoomTypeList() throws Exception {
        when(roomTypeService.getRoomTypeList()).thenReturn(roomTypeList);

        MockHttpServletRequestBuilder request = get(url + "/all")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void RoomTypeController_GetRoomTypeNameList_ReturnsStringList() throws Exception {
        when(roomTypeService.getRoomTypeNameList()).thenReturn(roomTypeNameList);

        MockHttpServletRequestBuilder request = get(url + "/all/names")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void RoomTypeController_GetRoomTypeEquipmentById_ReturnsStringList() throws Exception {
        when(roomTypeService.getRoomTypeEquipmentById(Mockito.any(long.class))).thenReturn(roomType.getEquipment());

        MockHttpServletRequestBuilder request = get(url + "/id/" + roomType.getId() + "/equipment")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void RoomTypeController_GetRoomTypeImageById_ReturnsFileResponseDto() throws Exception {
        when(roomTypeService.getRoomTypeImageById(Mockito.any(long.class))).thenReturn(fileResponseDto);

        MockHttpServletRequestBuilder request = get(url + "/id/" + roomType.getId() + "/image")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.file_name", CoreMatchers.is(fileResponseDto.getFileName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.encoded_file", CoreMatchers.is(fileResponseDto.getEncodedFile())));
    }

    @Test
    public void RoomTypeController_CreateRoomTypeImage_ReturnsFileResponseDto() throws Exception {
        MockMultipartFile file = new MockMultipartFile("image", "test.txt", "text/plain", HexFormat.of().parseHex("a04fd020ea3a6910a2d808002b30309d"));
        fileResponseDto.setFileName("room-type-image");
        fileResponseDto.setFile(HexFormat.of().parseHex("e04fd020ea3a6910a2d808002b30309d"));

        when(roomTypeService.createRoomTypeImage(Mockito.any(MultipartFile.class))).thenReturn(fileResponseDto);

        MockHttpServletRequestBuilder request = multipart(url + "/image")
                .file(file);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.file_name", CoreMatchers.is("room-type-image")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.encoded_file", CoreMatchers.is(fileResponseDto.getEncodedFile())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.file", CoreMatchers.is(fileResponseDto.getEncodedFile())));
    }
}
