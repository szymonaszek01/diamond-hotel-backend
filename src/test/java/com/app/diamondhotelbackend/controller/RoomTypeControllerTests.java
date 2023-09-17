package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.entity.RoomType;
import com.app.diamondhotelbackend.security.jwt.JwtFilter;
import com.app.diamondhotelbackend.service.roomtype.RoomTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = RoomTypeController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class RoomTypeControllerTests {

    @MockBean
    private RoomTypeServiceImpl roomTypeService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private MockMvc mockMvc;

    private RoomType roomType;

    private List<RoomType> roomTypeList;

    private List<String> roomTypeNameList;

    private static final String url = "/api/v1/room-type";

    @BeforeEach
    public void init() {
        roomType = RoomType.builder()
                .id(1)
                .name("Deluxe Suite")
                .adults(2)
                .children(0)
                .pricePerHotelNight(BigDecimal.valueOf(350))
                .image("https://publish.purewow.net/wp-content/uploads/sites/2/2019/08/grand-velas.jpeg?fit=1360%2C906")
                .equipment(Arrays.asList("King size bed", "Sofa bed", "Personalized Climate Control", "Minibar", "Balcony"))
                .build();

        roomTypeList = List.of(
                RoomType.builder()
                        .name("Deluxe Suite")
                        .adults(2)
                        .children(0)
                        .pricePerHotelNight(BigDecimal.valueOf(350))
                        .image("https://publish.purewow.net/wp-content/uploads/sites/2/2019/08/grand-velas.jpeg?fit=1360%2C906")
                        .build(),
                RoomType.builder()
                        .name("Family Room")
                        .adults(2)
                        .children(2)
                        .pricePerHotelNight(BigDecimal.valueOf(200))
                        .image("https://images.unsplash.com/photo-1578683010236-d716f9a3f461?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8bHV4dXJ5JTIwYmVkcm9vbXxlbnwwfHwwfHw%3D&w=1000&q=80")
                        .build()
        );

        roomTypeNameList = List.of("Deluxe Suite", "Family Room");
    }

    @Test
    public void RoomTypeController_GetRoomTypeById_ReturnsRoomType() throws Exception {
        when(roomTypeService.getRoomTypeById(roomType.getId())).thenReturn(roomType);

        MockHttpServletRequestBuilder request = get(url + "/id/" + roomType.getId())
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
        when(roomTypeService.getRoomTypeEquipmentById(roomType.getId())).thenReturn(roomType.getEquipment());

        MockHttpServletRequestBuilder request = get(url + "/id/" + roomType.getId() + "/equipment")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
