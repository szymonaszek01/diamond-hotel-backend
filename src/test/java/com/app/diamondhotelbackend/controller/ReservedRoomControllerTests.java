package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.ReservedRoom;
import com.app.diamondhotelbackend.entity.Room;
import com.app.diamondhotelbackend.security.jwt.JwtFilter;
import com.app.diamondhotelbackend.service.reservedroom.ReservedRoomServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
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

@WebMvcTest(controllers = ReservedRoomController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class ReservedRoomControllerTests {

    @MockBean
    private ReservedRoomServiceImpl reservedRoomService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private MockMvc mockMvc;

    private List<ReservedRoom> reservedRoomList;

    private static final String url = "/api/v1/reserved-room";

    @BeforeEach
    public void init() {
        Reservation reservation = Reservation.builder()
                .id(1)
                .checkIn(Date.valueOf("2023-12-24"))
                .checkOut(Date.valueOf("2023-12-27"))
                .adults(2)
                .children(2)
                .build();

        List<Room> roomList = List.of(
                Room.builder()
                        .id(1)
                        .build(),
                Room.builder()
                        .id(2)
                        .build()
        );

        reservedRoomList = List.of(
                ReservedRoom.builder()
                        .id(1)
                        .reservation(reservation)
                        .room(roomList.get(0))
                        .build(),
                ReservedRoom.builder()
                        .id(2)
                        .reservation(reservation)
                        .room(roomList.get(1))
                        .build()
        );
    }

    @Test
    public void PaymentController_GetReservedRoomList_ReturnsPaymentList() throws Exception {
        when(reservedRoomService.getReservedRoomList(Mockito.any(int.class), Mockito.any(int.class), Mockito.any(String.class), Mockito.any(JSONArray.class))).thenReturn(reservedRoomList);

        MockHttpServletRequestBuilder request = get(url + "/all")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("page", "0")
                .queryParam("size", "3");

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(reservedRoomList.size())));
    }

    @Test
    public void ReservedRoomController_GetReservedRoomListByUserProfileId_ReturnsReservedRoomList() throws Exception {
        when(reservedRoomService.getReservedRoomListByUserProfileId(Mockito.any(long.class), Mockito.any(int.class), Mockito.any(int.class), Mockito.any(String.class), Mockito.any(JSONArray.class))).thenReturn(reservedRoomList);

        MockHttpServletRequestBuilder request = get(url + "/all/user-profile-id/" + 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("page", "0")
                .queryParam("size", "3")
                .queryParam("status", ConstantUtil.APPROVED);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(reservedRoomList.size())));
    }

    @Test
    public void ReservedRoomController_CountReservedRoomListByUserProfileId_Long() throws Exception {
        when(reservedRoomService.countReservedRoomListByUserProfileId(Mockito.any(long.class))).thenReturn(2L);

        MockHttpServletRequestBuilder request = get(url + "/all/number/user-profile-id/" + 1L)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.is("2")));
    }
}
