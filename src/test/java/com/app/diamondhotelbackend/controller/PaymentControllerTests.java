package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.payment.request.PaymentCancelRequestDto;
import com.app.diamondhotelbackend.dto.payment.request.PaymentChargeRequestDto;
import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.security.jwt.JwtFilter;
import com.app.diamondhotelbackend.service.payment.PaymentServiceImpl;
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

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class PaymentControllerTests {

    @MockBean
    private PaymentServiceImpl paymentService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentChargeRequestDto paymentChargeRequestDto;

    private PaymentCancelRequestDto paymentCancelRequestDto;

    private Payment payment;

    private List<Payment> paymentList;

    private static final String url = "/api/v1/payment";

    @BeforeEach
    public void init() {
        paymentChargeRequestDto = PaymentChargeRequestDto.builder()
                .paymentId(1)
                .reservationId(1)
                .userProfileId(1)
                .token("token1")
                .amount(200)
                .build();

        paymentCancelRequestDto = PaymentCancelRequestDto.builder()
                .paymentId(1)
                .reservationId(1)
                .userProfileId(1)
                .build();

        payment = Payment.builder()
                .id(1)
                .createdAt(new Date(System.currentTimeMillis()))
                .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                .status(ConstantUtil.APPROVED)
                .charge("chargeId1")
                .token("token1")
                .cost(BigDecimal.valueOf(200))
                .build();

        paymentList = List.of(
                Payment.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .token("token1")
                        .build(),
                Payment.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .token("token2")
                        .build(),
                Payment.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .token("token3")
                        .build()
        );
    }

    @Test
    public void PaymentController_GetPaymentList_ReturnsPaymentList() throws Exception {
        when(paymentService.getPaymentList(Mockito.any(int.class), Mockito.any(int.class))).thenReturn(paymentList);

        MockHttpServletRequestBuilder request = get(url + "/all")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("page", "0")
                .queryParam("size", "3");

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(paymentList.size())));
    }

    @Test
    public void PaymentController_GetPaymentListByUserProfileId_ReturnsPaymentList() throws Exception {
        when(paymentService.getPaymentListByUserProfileId(Mockito.any(long.class), Mockito.any(int.class), Mockito.any(int.class), Mockito.any(String.class))).thenReturn(paymentList);

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(paymentList.size())));
    }

    @Test
    public void PaymentController_CountPaymentListByUserProfileId_ReturnsLong() throws Exception {
        when(paymentService.countPaymentListByUserProfileId(Mockito.any(long.class))).thenReturn(3L);

        MockHttpServletRequestBuilder request = get(url + "/all/number/user-profile-id/" + 1L)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(CoreMatchers.is("3")));
    }

    @Test
    public void PaymentController_ChargePayment_ReturnsPayment() throws Exception {
        when(paymentService.chargePayment(Mockito.any(PaymentChargeRequestDto.class))).thenReturn(payment);

        MockHttpServletRequestBuilder request = put(url + "/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentChargeRequestDto));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(payment.getStatus())));
    }

    @Test
    public void PaymentController_CancelPayment_ReturnsPayment() throws Exception {
        payment.setStatus(ConstantUtil.CANCELLED);

        when(paymentService.cancelPayment(Mockito.any(PaymentCancelRequestDto.class))).thenReturn(payment);

        MockHttpServletRequestBuilder request = put(url + "/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentCancelRequestDto));

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", CoreMatchers.is(payment.getStatus())));
    }
}
