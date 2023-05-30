package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.reservation.UserReservationAllRequestDto;
import com.app.diamondhotelbackend.dto.reservation.UserReservationAllResponseDto;
import com.app.diamondhotelbackend.dto.reservation.UserReservationInfoDto;
import com.app.diamondhotelbackend.entity.Reservation;
import com.app.diamondhotelbackend.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final FilterService filterService;

    public UserReservationAllResponseDto getUserReservationInfoList(UserReservationAllRequestDto userReservationAllRequestDto) {
        List<UserReservationInfoDto> userReservationInfoDtoList = reservationRepository.findAllByUserProfileId(userReservationAllRequestDto.getUserProfileId())
                .stream()
                .map(this::toUserReservationInfoDtoMapper)
                .toList();

        userReservationInfoDtoList = filterUserReservationInfoDtoListByCheckIn(userReservationInfoDtoList, userReservationAllRequestDto.getCheckIn());
        userReservationInfoDtoList = filterUserReservationInfoDtoListByCheckOut(userReservationInfoDtoList, userReservationAllRequestDto.getCheckOut());
        userReservationInfoDtoList = filterUserReservationInfoDtoListByRoomTypeName(userReservationInfoDtoList, userReservationAllRequestDto.getRoomTypeName());
        userReservationInfoDtoList = filterUserReservationInfoDtoListByCapacity(userReservationInfoDtoList, userReservationAllRequestDto.getCapacity());

        return UserReservationAllResponseDto.builder().
                userProfileId(userReservationAllRequestDto.getUserProfileId())
                .userReservationInfoDtoList(userReservationInfoDtoList)
                .build();
    }

    private UserReservationInfoDto toUserReservationInfoDtoMapper(Reservation reservation) {
        return UserReservationInfoDto.builder().id(reservation.getId()).code(reservation.getCode()).roomType(reservation.getRoom().getRoomType().getName()).checkIn(reservation.getCheckIn().toString()).checkOut(reservation.getCheckOut().toString()).capacity(reservation.getRoom().getRoomType().getCapacity()).cost(BigDecimal.valueOf(getTotalCost(reservation))).build();
    }

    private double getTotalCost(Reservation reservation) {
        return reservation.getTotalWithoutTax().doubleValue() + reservation.getTax().doubleValue() + reservation.getCarPickUp().doubleValue() + reservation.getCarRent().doubleValue();
    }

    private List<UserReservationInfoDto> filterUserReservationInfoDtoListByCheckIn(List<UserReservationInfoDto> userReservationInfoDtoList, String checkIn) {
        Optional<LocalDateTime> checkInUser = filterService.isValidCheckInOrCheckOut(checkIn);
        if (checkInUser.isEmpty()) {
            return userReservationInfoDtoList;
        }

        return userReservationInfoDtoList
                .stream()
                .filter(userReservationInfoDto -> {
                    Optional<LocalDateTime> checkInReservation = filterService.isValidCheckInOrCheckOut(userReservationInfoDto.getCheckIn());
                    return checkInReservation.isPresent() && checkInReservation.get().isAfter(checkInUser.get());
                })
                .toList();
    }

    private List<UserReservationInfoDto> filterUserReservationInfoDtoListByCheckOut(List<UserReservationInfoDto> userReservationInfoDtoList, String checkOut) {
        Optional<LocalDateTime> checkOutUser = filterService.isValidCheckInOrCheckOut(checkOut);
        if (checkOutUser.isEmpty()) {
            return userReservationInfoDtoList;
        }

        return userReservationInfoDtoList
                .stream()
                .filter(userReservationInfoDto -> {
                    Optional<LocalDateTime> checkOutReservation = filterService.isValidCheckInOrCheckOut(userReservationInfoDto.getCheckIn());
                    return checkOutReservation.isPresent() && checkOutReservation.get().isBefore(checkOutUser.get());
                })
                .toList();
    }

    private List<UserReservationInfoDto> filterUserReservationInfoDtoListByRoomTypeName(List<UserReservationInfoDto> userReservationInfoDtoList, String roomTypeName) {
        Optional<String> roomTypeNameUser = filterService.isValidRoomTypeName(roomTypeName);
        if (roomTypeNameUser.isEmpty()) {
            return userReservationInfoDtoList;
        }

        return userReservationInfoDtoList
                .stream()
                .filter(userReservationInfoDto -> roomTypeNameUser.get().equals(userReservationInfoDto.getRoomType()))
                .toList();
    }

    private List<UserReservationInfoDto> filterUserReservationInfoDtoListByCapacity(List<UserReservationInfoDto> userReservationInfoDtoList, String capacity) {
        Optional<Integer> capacityUser = filterService.isValidCapacity(capacity);
        if (capacityUser.isEmpty()) {
            return userReservationInfoDtoList;
        }

        return userReservationInfoDtoList
                .stream()
                .filter(userReservationInfoDto -> capacityUser.get().equals(userReservationInfoDto.getCapacity()))
                .toList();
    }
}
