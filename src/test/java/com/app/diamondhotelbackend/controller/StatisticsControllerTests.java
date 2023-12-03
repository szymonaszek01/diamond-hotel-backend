package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.statistics.model.StatisticsData;
import com.app.diamondhotelbackend.dto.statistics.request.StatisticsRequestDto;
import com.app.diamondhotelbackend.dto.statistics.response.StatisticsResponseDto;
import com.app.diamondhotelbackend.security.jwt.JwtFilter;
import com.app.diamondhotelbackend.service.statistics.StatisticsServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(controllers = StatisticsController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class StatisticsControllerTests {

    private static final String url = "/api/v1/statistics";

    @MockBean
    private StatisticsServiceImpl statisticsService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private MockMvc mockMvc;

    private List<Integer> yearList;

    private List<String> monthList;

    private StatisticsResponseDto statisticsResponseDto;

    @BeforeEach
    public void init() {
        List<StatisticsData> statisticsDataList = new ArrayList<>();
        ConstantUtil.STATISTICS_MONTH_LIST.forEach(month -> statisticsDataList.add(StatisticsData.builder().x(month).y(0).build()));

        yearList = List.of(2023, 2024);

        monthList = new ArrayList<>(List.of(ConstantUtil.STATISTICS_NONE));
        monthList.addAll(ConstantUtil.STATISTICS_MONTH_LIST);

        statisticsResponseDto = StatisticsResponseDto.builder()
                .avg(0)
                .statisticsDataList(statisticsDataList)
                .build();
    }

    @Test
    public void StatisticsController_GetYearStatistics_ReturnsIntegerList() throws Exception {
        when(statisticsService.getYearStatistics()).thenReturn(yearList);

        MockHttpServletRequestBuilder request = get(url + "/year")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0]", CoreMatchers.is(yearList.get(0))));
    }

    @Test
    public void StatisticsController_GetMonthStatistics_ReturnsStringList() throws Exception {
        when(statisticsService.getMonthStatistics()).thenReturn(monthList);

        MockHttpServletRequestBuilder request = get(url + "/month")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()", CoreMatchers.is(13)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[2]", CoreMatchers.is(monthList.get(2))));
    }

    @Test
    public void StatisticsController_GetSummaryStatistics_ReturnsStatisticsResponseDto() throws Exception {
        List<StatisticsData> statisticsDataList = List.of(
                StatisticsData.builder().x(ConstantUtil.STATISTICS_USERS).y(123).build(),
                StatisticsData.builder().x(ConstantUtil.STATISTICS_RESERVATIONS).y(100).build(),
                StatisticsData.builder().x(ConstantUtil.STATISTICS_RESERVED_ROOMS).y(200).build(),
                StatisticsData.builder().x(ConstantUtil.STATISTICS_USERS).y(40000).build()
        );
        statisticsResponseDto.setStatisticsDataList(statisticsDataList);

        when(statisticsService.getSummaryStatistics(Mockito.any(StatisticsRequestDto.class))).thenReturn(statisticsResponseDto);

        MockHttpServletRequestBuilder request = get(url + "/summary")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("year", "2023");

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.size()", CoreMatchers.is(4)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].x", CoreMatchers.is(ConstantUtil.STATISTICS_RESERVED_ROOMS)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].y", CoreMatchers.is(200)));
    }

    @Test
    public void StatisticsController_GetRoomTypeStatistics_ReturnsStatisticsResponseDto() throws Exception {
        List<StatisticsData> statisticsDataList = List.of(
                StatisticsData.builder().x("Deluxe Suite").y(123).build(),
                StatisticsData.builder().x("Family room").y(100).build()
        );
        statisticsResponseDto.setStatisticsDataList(statisticsDataList);

        when(statisticsService.getRoomTypeStatistics(Mockito.any(StatisticsRequestDto.class))).thenReturn(statisticsResponseDto);

        MockHttpServletRequestBuilder request = get(url + "/room-type")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("year", "2023");

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.size()", CoreMatchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].x", CoreMatchers.is("Family room")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[1].y", CoreMatchers.is(100)));
    }

    @Test
    public void StatisticsController_GetUserProfileStatistics_ReturnsStatisticsResponseDto() throws Exception {
        List<StatisticsData> statisticsDataList = statisticsResponseDto.getStatisticsDataList();
        statisticsDataList.get(2).setY(123);
        statisticsResponseDto.setStatisticsDataList(statisticsDataList);
        statisticsResponseDto.setAvg((double) 123 / 12);

        when(statisticsService.getUserProfileStatistics(Mockito.any(StatisticsRequestDto.class))).thenReturn(statisticsResponseDto);

        MockHttpServletRequestBuilder request = get(url + "/user-profile")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("year", "2023");

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.size()", CoreMatchers.is(12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].x", CoreMatchers.is(ConstantUtil.STATISTICS_MONTH_LIST.get(2))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].y", CoreMatchers.is(123)));
    }

    @Test
    public void StatisticsController_GetReservationStatistics_ReturnsStatisticsResponseDto() throws Exception {
        List<StatisticsData> statisticsDataList = statisticsResponseDto.getStatisticsDataList();
        statisticsDataList.get(2).setY(123);
        statisticsResponseDto.setStatisticsDataList(statisticsDataList);
        statisticsResponseDto.setAvg((double) 123 / 12);

        when(statisticsService.getReservationStatistics(Mockito.any(StatisticsRequestDto.class))).thenReturn(statisticsResponseDto);

        MockHttpServletRequestBuilder request = get(url + "/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("year", "2023");

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.size()", CoreMatchers.is(12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].x", CoreMatchers.is(ConstantUtil.STATISTICS_MONTH_LIST.get(2))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].y", CoreMatchers.is(123)));
    }

    @Test
    public void StatisticsController_GetReservedRoomStatistics_ReturnsStatisticsResponseDto() throws Exception {
        List<StatisticsData> statisticsDataList = statisticsResponseDto.getStatisticsDataList();
        statisticsDataList.get(2).setY(123);
        statisticsResponseDto.setStatisticsDataList(statisticsDataList);
        statisticsResponseDto.setAvg((double) 123 / 12);

        when(statisticsService.getReservedRoomStatistics(Mockito.any(StatisticsRequestDto.class))).thenReturn(statisticsResponseDto);

        MockHttpServletRequestBuilder request = get(url + "/reserved-room")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("year", "2023");

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.size()", CoreMatchers.is(12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].x", CoreMatchers.is(ConstantUtil.STATISTICS_MONTH_LIST.get(2))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].y", CoreMatchers.is(123)));
    }

    @Test
    public void StatisticsController_GetIncomeStatistics_ReturnsStatisticsResponseDto() throws Exception {
        List<StatisticsData> statisticsDataList = statisticsResponseDto.getStatisticsDataList();
        statisticsDataList.get(2).setY(120000);
        statisticsResponseDto.setStatisticsDataList(statisticsDataList);
        statisticsResponseDto.setAvg((double) 120000 / 12);

        when(statisticsService.getIncomeStatistics(Mockito.any(StatisticsRequestDto.class))).thenReturn(statisticsResponseDto);

        MockHttpServletRequestBuilder request = get(url + "/income")
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("year", "2023");

        mockMvc
                .perform(request)
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.size()", CoreMatchers.is(12)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].x", CoreMatchers.is(ConstantUtil.STATISTICS_MONTH_LIST.get(2))))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data[2].y", CoreMatchers.is(120000)));

    }
}
