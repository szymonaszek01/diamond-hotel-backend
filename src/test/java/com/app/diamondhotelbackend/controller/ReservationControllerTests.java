package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.reservation.request.ReservationCreateRequestDto;
import com.app.diamondhotelbackend.dto.room.model.RoomSelected;
import com.app.diamondhotelbackend.entity.Flight;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.Transaction;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.security.jwt.JwtFilter;
import com.app.diamondhotelbackend.service.reservation.ReservationServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class ReservationControllerTests {

    @MockBean
    private ReservationServiceImpl reservationService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ReservationCreateRequestDto reservationCreateRequestDto;

    private Reservation reservation;

    private static final String url = "/api/v1/reservation";

    @BeforeEach
    public void init() {
        List<RoomSelected> roomSelectedList = List.of(
                RoomSelected.builder()
                        .roomTypeId(1)
                        .rooms(1)
                        .build(),
                RoomSelected.builder()
                        .roomTypeId(2)
                        .rooms(1)
                        .build()
        );

        reservationCreateRequestDto = ReservationCreateRequestDto.builder()
                .userProfileId(1)
                .checkIn("2023-12-24")
                .checkOut("2023-12-27")
                .adults(2)
                .children(2)
                .flightNumber("flightNumber1")
                .roomSelectedList(roomSelectedList)
                .build();

        UserProfile userProfile = UserProfile.builder()
                .id(reservationCreateRequestDto.getUserProfileId())
                .email("email1")
                .passportNumber("passportNumber1")
                .build();

        Flight flight = Flight.builder()
                .id(1)
                .flightNumber(reservationCreateRequestDto.getFlightNumber())
                .build();

        Transaction transaction = Transaction.builder()
                .id(1)
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .token("token1")
                .status(ConstantUtil.WAITING_FOR_PAYMENT)
                .build();

        reservation = Reservation.builder()
                .id(1)
                .checkIn(Date.valueOf(reservationCreateRequestDto.getCheckIn()))
                .checkOut(Date.valueOf(reservationCreateRequestDto.getCheckOut()))
                .adults(reservationCreateRequestDto.getAdults())
                .children(reservationCreateRequestDto.getChildren())
                .userProfile(userProfile)
                .flight(flight)
                .transaction(transaction)
                .build();
    }

    @Test
    public void ReservationController_CreateReservation_ReturnsReservation() throws Exception {
        when(reservationService.createReservation(Mockito.any(ReservationCreateRequestDto.class))).thenReturn(reservation);

        MockHttpServletRequestBuilder request = post(url + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reservationCreateRequestDto));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is((int) reservation.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.check_in", CoreMatchers.is(reservation.getCheckIn().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.check_out", CoreMatchers.is(reservation.getCheckOut().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user_profile.id", CoreMatchers.is((int) reservation.getUserProfile().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.flight.id", CoreMatchers.is((int) reservation.getFlight().getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.transaction.id", CoreMatchers.is((int) reservation.getTransaction().getId())));
    }
}
