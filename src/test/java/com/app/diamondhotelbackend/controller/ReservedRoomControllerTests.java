package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.ReservedRoom;
import com.app.diamondhotelbackend.entity.Room;
import com.app.diamondhotelbackend.security.jwt.JwtFilter;
import com.app.diamondhotelbackend.service.reservedroom.ReservedRoomServiceImpl;
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

    private Reservation reservation;

    private List<ReservedRoom> reservedRoomList;

    private static final String url = "/api/v1/reserved-room";

    @BeforeEach
    public void init() {
        reservation = Reservation.builder()
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
    public void ReservationController_CreateReservation_ReturnsReservation() throws Exception {
        when(reservedRoomService.getReservedRoomListByReservationId(Mockito.any(long.class))).thenReturn(reservedRoomList);

        MockHttpServletRequestBuilder request = get(url + "/all/reservation/id/" + reservation.getId())
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id", CoreMatchers.is((int) reservedRoomList.get(0).getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id", CoreMatchers.is((int) reservedRoomList.get(1).getId())));
    }
}
