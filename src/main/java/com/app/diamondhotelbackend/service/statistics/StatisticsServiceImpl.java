package com.app.diamondhotelbackend.service.statistics;

import com.app.diamondhotelbackend.dto.statistics.model.StatisticsData;
import com.app.diamondhotelbackend.dto.statistics.model.StatisticsDataDayAction;
import com.app.diamondhotelbackend.dto.statistics.model.StatisticsDataMonthAction;
import com.app.diamondhotelbackend.dto.statistics.request.StatisticsRequestDto;
import com.app.diamondhotelbackend.dto.statistics.response.StatisticsResponseDto;
import com.app.diamondhotelbackend.entity.Payment;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.entity.ReservedRoom;
import com.app.diamondhotelbackend.entity.UserProfile;
import com.app.diamondhotelbackend.service.payment.PaymentServiceImpl;
import com.app.diamondhotelbackend.service.reservation.ReservationServiceImpl;
import com.app.diamondhotelbackend.service.reservedroom.ReservedRoomServiceImpl;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.app.diamondhotelbackend.util.DateUtil.*;

@Service
@AllArgsConstructor
@Slf4j
public class StatisticsServiceImpl implements StatisticsService {

    private final UserProfileServiceImpl userProfileService;

    private final ReservationServiceImpl reservationService;

    private final ReservedRoomServiceImpl reservedRoomService;

    private final PaymentServiceImpl paymentService;

    @Override
    public List<Integer> getYearStatistics() {
        List<Integer> yearList = new ArrayList<>(userProfileService.getUserProfileCreatedAtYearList());
        yearList.addAll(reservationService.getReservationCheckInAndCheckOutYearList());

        return yearList.stream()
                .distinct()
                .toList();
    }

    @Override
    public List<String> getMonthStatistics() {
        List<String> monthList = new ArrayList<>();
        monthList.add(ConstantUtil.STATISTICS_NONE);
        monthList.addAll(ConstantUtil.STATISTICS_MONTH_LIST);

        return monthList;
    }

    @Override
    public StatisticsResponseDto getSummaryStatistics(StatisticsRequestDto statisticsRequestDto) {
        long userProfileCount = userProfileService.getUserProfileList(statisticsRequestDto.getMin(), statisticsRequestDto.getMax()).size();
        long reservationCount = reservationService.getReservationList(statisticsRequestDto.getMin(), statisticsRequestDto.getMax()).size();
        long reservedRoomCount = reservedRoomService.getReservedRoomList(statisticsRequestDto.getMin(), statisticsRequestDto.getMax()).size();
        long paymentCostSum = toPaymentCostSumMapper(paymentService.getPaymentList(statisticsRequestDto.getMin(), statisticsRequestDto.getMax()).stream());

        List<StatisticsData> statisticsDataList = List.of(
                StatisticsData.builder().x(ConstantUtil.STATISTICS_USERS).y(userProfileCount).build(),
                StatisticsData.builder().x(ConstantUtil.STATISTICS_RESERVATIONS).y(reservationCount).build(),
                StatisticsData.builder().x(ConstantUtil.STATISTICS_RESERVED_ROOMS).y(reservedRoomCount).build(),
                StatisticsData.builder().x(ConstantUtil.STATISTICS_INCOME).y(paymentCostSum).build()
        );

        return toStatisticsResponseDtoMapper(statisticsDataList, 0);
    }

    @Override
    public StatisticsResponseDto getRoomTypeStatistics(StatisticsRequestDto statisticsRequestDto) {
        List<ReservedRoom> reservedRoomList = reservedRoomService.getReservedRoomList(statisticsRequestDto.getMin(), statisticsRequestDto.getMax());
        List<StatisticsData> statisticsDataList = reservedRoomList.stream()
                .collect(Collectors.groupingBy(reservedRoom -> reservedRoom.getRoom().getRoomType().getName(), Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> StatisticsData.builder().x(entry.getKey()).y(entry.getValue()).build())
                .toList();

        return toStatisticsResponseDtoMapper(statisticsDataList, 0);
    }

    @Override
    public StatisticsResponseDto getUserProfileStatistics(StatisticsRequestDto statisticsRequestDto) {
        List<UserProfile> userProfileList = userProfileService.getUserProfileList(statisticsRequestDto.getMin(), statisticsRequestDto.getMax());
        StatisticsDataDayAction statisticsDataDayAction = (day) -> userProfileList.stream()
                .filter(userProfile -> isDateEqual(statisticsRequestDto.getDateInMillis(day), userProfile.getCreatedAt()))
                .count();
        StatisticsDataMonthAction statisticsDataMonthAction = (month) -> userProfileList.stream()
                .filter(userProfile -> isMonthEqual(month, userProfile.getCreatedAt()))
                .count();

        return toStatisticsResponseDtoMapper(statisticsRequestDto, statisticsDataMonthAction, statisticsDataDayAction);
    }

    @Override
    public StatisticsResponseDto getReservationStatistics(StatisticsRequestDto statisticsRequestDto) {
        List<Reservation> reservationList = reservationService.getReservationList(statisticsRequestDto.getMin(), statisticsRequestDto.getMax());
        StatisticsDataDayAction statisticsDataDayAction = (day) -> reservationList.stream()
                .filter(reservation -> isDateBetween(statisticsRequestDto.getDateInMillis(day), reservation.getCheckIn(), reservation.getCheckOut()))
                .count();
        StatisticsDataMonthAction statisticsDataMonthAction = (month) -> reservationList.stream()
                .filter(reservation -> isMonthEqual(month, reservation.getCheckIn(), reservation.getCheckOut()))
                .count();

        return toStatisticsResponseDtoMapper(statisticsRequestDto, statisticsDataMonthAction, statisticsDataDayAction);
    }

    @Override
    public StatisticsResponseDto getReservedRoomStatistics(StatisticsRequestDto statisticsRequestDto) {
        List<ReservedRoom> reservedRoomList = reservedRoomService.getReservedRoomList(statisticsRequestDto.getMin(), statisticsRequestDto.getMax());
        StatisticsDataDayAction statisticsDataDayAction = (day) -> reservedRoomList.stream()
                .filter(reservedRoom -> isDateBetween(statisticsRequestDto.getDateInMillis(day), reservedRoom.getReservation().getCheckIn(), reservedRoom.getReservation().getCheckOut()))
                .count();
        StatisticsDataMonthAction statisticsDataMonthAction = (month) -> reservedRoomList.stream()
                .filter(reservedRoom -> isMonthEqual(month, reservedRoom.getReservation().getCheckIn(), reservedRoom.getReservation().getCheckOut()))
                .count();

        return toStatisticsResponseDtoMapper(statisticsRequestDto, statisticsDataMonthAction, statisticsDataDayAction);
    }

    @Override
    public StatisticsResponseDto getIncomeStatistics(StatisticsRequestDto statisticsRequestDto) {
        List<Payment> paymentList = paymentService.getPaymentList(statisticsRequestDto.getMin(), statisticsRequestDto.getMax());
        StatisticsDataDayAction statisticsDataDayAction = (day) -> toPaymentCostSumMapper(paymentList.stream()
                .filter(payment -> isDateEqual(statisticsRequestDto.getDateInMillis(day), toSqlDateMapper(payment.getCreatedAt()))));
        StatisticsDataMonthAction statisticsDataMonthAction = (month) -> toPaymentCostSumMapper(paymentList.stream()
                .filter(payment -> isMonthEqual(month, new Date(payment.getCreatedAt().getTime()))));

        return toStatisticsResponseDtoMapper(statisticsRequestDto, statisticsDataMonthAction, statisticsDataDayAction);
    }

    private long toPaymentCostSumMapper(Stream<Payment> paymentStream) {
        return paymentStream
                .map(Payment::getCost)
                .mapToLong(BigDecimal::longValue)
                .sum();
    }

    private List<StatisticsData> toStatisticsDataListMapper(StatisticsRequestDto statisticsRequestDto, StatisticsDataDayAction statisticsDataDayAction) {
        List<StatisticsData> statisticsDataList = new ArrayList<>();
        IntStream.rangeClosed(1, statisticsRequestDto.getLastDayOfTheMonth())
                .forEach(day -> {
                    long y = statisticsDataDayAction.action(day);
                    statisticsDataList.add(StatisticsData.builder().x(String.valueOf(day)).y(y).build());
                });

        return statisticsDataList;
    }

    private List<StatisticsData> toStatisticsDataListMapper(StatisticsDataMonthAction statisticsDataMonthAction) {
        List<StatisticsData> statisticsDataList = new ArrayList<>();
        ConstantUtil.STATISTICS_MONTH_LIST
                .forEach(month -> {
                    long y = statisticsDataMonthAction.action(month);
                    statisticsDataList.add(StatisticsData.builder().x(month).y(y).build());
                });

        return statisticsDataList;
    }

    private double toAverageValueMapper(List<StatisticsData> statisticsDataList) {
        return statisticsDataList.stream()
                .map(StatisticsData::getY)
                .mapToLong(value -> value)
                .average()
                .orElse(0);
    }

    private StatisticsResponseDto toStatisticsResponseDtoMapper(StatisticsRequestDto statisticsRequestDto, StatisticsDataMonthAction statisticsDataMonthAction, StatisticsDataDayAction statisticsDataDayAction) {
        List<StatisticsData> statisticsDataList = (statisticsRequestDto.getMonth() > 0) ? toStatisticsDataListMapper(statisticsRequestDto, statisticsDataDayAction) :
                toStatisticsDataListMapper(statisticsDataMonthAction);
        double avg = toAverageValueMapper(statisticsDataList);

        return StatisticsResponseDto.builder()
                .avg(avg)
                .statisticsDataList(statisticsDataList)
                .build();
    }

    private StatisticsResponseDto toStatisticsResponseDtoMapper(List<StatisticsData> statisticsDataList, double avg) {
        return StatisticsResponseDto.builder()
                .avg(avg)
                .statisticsDataList(statisticsDataList)
                .build();
    }
}
