package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.statistics.request.StatisticsRequestDto;
import com.app.diamondhotelbackend.dto.statistics.response.StatisticsResponseDto;
import com.app.diamondhotelbackend.entity.*;
import com.app.diamondhotelbackend.service.payment.PaymentServiceImpl;
import com.app.diamondhotelbackend.service.reservation.ReservationServiceImpl;
import com.app.diamondhotelbackend.service.reservedroom.ReservedRoomServiceImpl;
import com.app.diamondhotelbackend.service.statistics.StatisticsServiceImpl;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatisticsServiceTests {

    @InjectMocks
    private StatisticsServiceImpl statisticsService;

    @Mock
    private UserProfileServiceImpl userProfileService;

    @Mock
    private ReservationServiceImpl reservationService;

    @Mock
    private ReservedRoomServiceImpl reservedRoomService;

    @Mock
    private PaymentServiceImpl paymentService;

    private List<Integer> yearList;

    private List<UserProfile> userProfileList;

    private List<Reservation> reservationList;

    private List<ReservedRoom> reservedRoomList;

    private List<Payment> paymentList;

    private StatisticsRequestDto statisticsRequestDto;

    @BeforeEach
    public void init() {
        yearList = List.of(2023, 3024);

        userProfileList = List.of(
                UserProfile.builder().id(1).createdAt(new Date(System.currentTimeMillis())).email("test1@gmail.com").build(),
                UserProfile.builder().id(2).createdAt(new Date(System.currentTimeMillis())).email("test2@gmail.com").build()
        );

        reservationList = List.of(
                Reservation.builder().id(1).checkIn(Date.valueOf("2024-01-01")).checkOut(Date.valueOf("2024-01-05")).build(),
                Reservation.builder().id(2).checkIn(Date.valueOf("2024-01-26")).checkOut(Date.valueOf("2024-01-31")).build()
        );

        reservedRoomList = List.of(
                ReservedRoom.builder()
                        .id(1)
                        .reservation(Reservation.builder().id(1).checkIn(Date.valueOf("2024-01-01")).checkOut(Date.valueOf("2024-01-05")).build())
                        .room(
                                Room.builder()
                                        .id(1)
                                        .roomType(
                                                RoomType.builder()
                                                        .id(1)
                                                        .name("Deluxe Suite")
                                                        .build()
                                        )
                                        .build()
                        )
                        .build(),
                ReservedRoom.builder()
                        .id(2)
                        .reservation(Reservation.builder().id(2).checkIn(Date.valueOf("2024-01-26")).checkOut(Date.valueOf("2024-01-31")).build())
                        .room(
                                Room.builder()
                                        .id(2)
                                        .roomType(
                                                RoomType.builder()
                                                        .id(2)
                                                        .name("Family Room")
                                                        .build()
                                        )
                                        .build()
                        )
                        .build()
        );

        paymentList = List.of(
                Payment.builder().id(1).createdAt(new java.util.Date(System.currentTimeMillis())).cost(BigDecimal.valueOf(5000)).build(),
                Payment.builder().id(2).createdAt(new java.util.Date(System.currentTimeMillis())).cost(BigDecimal.valueOf(10000)).build()
        );

        statisticsRequestDto = new StatisticsRequestDto(2024, 0);
    }

    @Test
    public void StatisticsService_GetYearStatistics_ReturnsIntegerList() {
        when(userProfileService.getUserProfileCreatedAtYearList()).thenReturn(yearList);
        when(reservationService.getReservationCheckInAndCheckOutYearList()).thenReturn(yearList);

        List<Integer> foundIntegerList = statisticsService.getYearStatistics();

        Assertions.assertThat(foundIntegerList).isNotNull();
        Assertions.assertThat(foundIntegerList.size()).isEqualTo(2);
    }

    @Test
    public void StatisticsService_GetMonthStatistics_ReturnsIntegerList() {
        List<String> foundStringList = statisticsService.getMonthStatistics();

        Assertions.assertThat(foundStringList).isNotNull();
        Assertions.assertThat(foundStringList.size()).isEqualTo(13);
    }

    @Test
    public void StatisticsService_GetSummaryStatistics_ReturnsStatisticsResponseDto() {
        when(userProfileService.getUserProfileList(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(userProfileList);
        when(reservationService.getReservationList(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(reservationList);
        when(reservedRoomService.getReservedRoomList(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(reservedRoomList);
        when(paymentService.getPaymentList(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(paymentList);

        StatisticsResponseDto foundStatisticsResponseDto = statisticsService.getSummaryStatistics(statisticsRequestDto);

        Assertions.assertThat(foundStatisticsResponseDto).isNotNull();
        Assertions.assertThat(foundStatisticsResponseDto.getStatisticsDataList().get(3).getX()).isEqualTo(ConstantUtil.STATISTICS_INCOME);
        Assertions.assertThat(foundStatisticsResponseDto.getStatisticsDataList().get(3).getY()).isEqualTo(15000);
    }

    @Test
    public void StatisticsService_GetRoomTypeStatistics_ReturnsStatisticsResponseDto() {
        when(reservedRoomService.getReservedRoomList(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(reservedRoomList);

        StatisticsResponseDto foundStatisticsResponseDto = statisticsService.getRoomTypeStatistics(statisticsRequestDto);

        Assertions.assertThat(foundStatisticsResponseDto).isNotNull();
        Assertions.assertThat(foundStatisticsResponseDto.getStatisticsDataList().get(0).getX()).isEqualTo("Family Room");
        Assertions.assertThat(foundStatisticsResponseDto.getStatisticsDataList().get(0).getY()).isEqualTo(1);
    }

    @Test
    public void StatisticsService_GetUserProfileStatistics_ReturnsStatisticsResponseDto() {
        when(userProfileService.getUserProfileList(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(userProfileList);

        StatisticsResponseDto foundStatisticsResponseDto = statisticsService.getUserProfileStatistics(statisticsRequestDto);

        Assertions.assertThat(foundStatisticsResponseDto).isNotNull();
        Assertions.assertThat(foundStatisticsResponseDto.getStatisticsDataList().get(0).getX()).isEqualTo(ConstantUtil.STATISTICS_MONTH_LIST.get(0));
        Assertions.assertThat(foundStatisticsResponseDto.getStatisticsDataList().get(0).getY()).isEqualTo(2);
    }

    @Test
    public void StatisticsService_GetReservationStatistics_ReturnsStatisticsResponseDto() {
        when(reservationService.getReservationList(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(reservationList);

        StatisticsResponseDto foundStatisticsResponseDto = statisticsService.getReservationStatistics(statisticsRequestDto);

        Assertions.assertThat(foundStatisticsResponseDto).isNotNull();
        Assertions.assertThat(foundStatisticsResponseDto.getStatisticsDataList().get(0).getX()).isEqualTo(ConstantUtil.STATISTICS_MONTH_LIST.get(0));
        Assertions.assertThat(foundStatisticsResponseDto.getStatisticsDataList().get(0).getY()).isEqualTo(2);
    }

    @Test
    public void StatisticsService_GetReservedRoomStatistics_ReturnsStatisticsResponseDto() {
        when(reservedRoomService.getReservedRoomList(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(reservedRoomList);

        StatisticsResponseDto foundStatisticsResponseDto = statisticsService.getReservedRoomStatistics(statisticsRequestDto);

        Assertions.assertThat(foundStatisticsResponseDto).isNotNull();
        Assertions.assertThat(foundStatisticsResponseDto.getStatisticsDataList().get(0).getX()).isEqualTo(ConstantUtil.STATISTICS_MONTH_LIST.get(0));
        Assertions.assertThat(foundStatisticsResponseDto.getStatisticsDataList().get(0).getY()).isEqualTo(2);
    }

    @Test
    public void StatisticsService_GetIncomeStatistics_ReturnsStatisticsResponseDto() {
        when(paymentService.getPaymentList(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(paymentList);

        StatisticsResponseDto foundStatisticsResponseDto = statisticsService.getIncomeStatistics(statisticsRequestDto);

        Assertions.assertThat(foundStatisticsResponseDto).isNotNull();
        Assertions.assertThat(foundStatisticsResponseDto.getStatisticsDataList().get(0).getX()).isEqualTo(ConstantUtil.STATISTICS_MONTH_LIST.get(0));
        Assertions.assertThat(foundStatisticsResponseDto.getStatisticsDataList().get(0).getY()).isEqualTo(15000);
    }
}
