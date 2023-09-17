package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.room.model.RoomAvailability;
import com.app.diamondhotelbackend.dto.room.model.RoomSelectedCost;
import com.app.diamondhotelbackend.dto.room.response.RoomAvailableResponseDto;
import com.app.diamondhotelbackend.dto.room.response.RoomSelectedCostResponseDto;
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

import java.math.BigDecimal;
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

    private RoomSelectedCostResponseDto roomSelectedCostResponseDto;

    private String checkIn;

    private String checkOut;

    private String rooms;

    private String adults;

    private String children;

    private String roomTypeId;

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

        checkIn = "2023-09-20";

        checkOut = "2023-09-21";

        rooms = String.valueOf(2);

        adults = String.valueOf(2);

        children = String.valueOf(2);

        roomTypeId = String.valueOf(1);

        RoomSelectedCost roomSelectedCost = RoomSelectedCost.builder()
                .roomTypeId(1)
                .rooms(1)
                .cost(BigDecimal.valueOf(350))
                .build();


        roomAvailableResponseDto = RoomAvailableResponseDto.builder()
                .checkIn(Date.valueOf(checkIn))
                .checkOut(Date.valueOf(checkOut))
                .roomAvailabilityList(roomAvailabilityList)
                .build();

        roomSelectedCostResponseDto = RoomSelectedCostResponseDto.builder()
                .checkIn(Date.valueOf(checkIn))
                .checkOut(Date.valueOf(checkOut))
                .roomSelectedCost(roomSelectedCost)
                .build();
    }

    @Test
    public void RoomController_GetRoomAvailabilityList_ReturnsRoomAvailableResponseDto() throws Exception {
        when(roomService.getRoomAvailableList(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(int.class), Mockito.any(int.class), Mockito.any(int.class), Mockito.anyList(), Mockito.any(double.class))).thenReturn(roomAvailableResponseDto);

        MockHttpServletRequestBuilder request = get(url + "/all/available")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("check-in", checkIn)
                .queryParam("check-out", checkOut)
                .queryParam("rooms", rooms)
                .queryParam("adults", adults)
                .queryParam("children", children);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void RoomController_GetRoomSelectedCostList_ReturnsRoomSelectedCostResponseDto() throws Exception {
        when(roomService.getRoomSelectedCost(Mockito.any(String.class), Mockito.any(String.class), Mockito.any(long.class), Mockito.any(int.class))).thenReturn(roomSelectedCostResponseDto);

        MockHttpServletRequestBuilder request = get(url + "/cost")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("check-in", checkIn)
                .queryParam("check-out", checkOut)
                .queryParam("rooms", rooms)
                .queryParam("room-type-id", roomTypeId);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
