package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.room.model.RoomAvailability;
import com.app.diamondhotelbackend.dto.room.response.RoomAvailableResponseDto;
import com.app.diamondhotelbackend.security.jwt.JwtFilter;
import com.app.diamondhotelbackend.service.room.RoomServiceImpl;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = RoomController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class RoomControllerTests {

    @MockBean
    private RoomServiceImpl roomService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private MockMvc mockMvc;

    private RoomAvailableResponseDto roomAvailableResponseDto;

    private static final String url = "/api/v1/room";

    @BeforeEach
    public void init() {
        List<RoomAvailability> roomAvailabilityList = List.of(
                RoomAvailability.builder()
                        .roomTypeId(1)
                        .availability(1)
                        .build(),
                RoomAvailability.builder()
                        .roomTypeId(2)
                        .availability(1)
                        .build()
        );

        roomAvailableResponseDto = RoomAvailableResponseDto.builder()
                .checkIn(Date.valueOf("2023-09-20"))
                .checkOut(Date.valueOf("2023-09-25"))
                .roomAvailabilityList(roomAvailabilityList)
                .build();
    }

    @Test
    public void RoomController_GetRoomAvailabilityList_ReturnsRoomAvailableResponseDto() throws Exception {
        when(roomService.getRoomAvailableList(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(int.class), Mockito.any(int.class), Mockito.any(int.class), Mockito.anyList(), Mockito.any(double.class))).thenReturn(roomAvailableResponseDto);

        MockHttpServletRequestBuilder request = get(url + "/all/available")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("check-in", "2023-09-20")
                .queryParam("check-out", "2023-09-25")
                .queryParam("rooms", "2")
                .queryParam("adults", "2")
                .queryParam("children", "2");

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
