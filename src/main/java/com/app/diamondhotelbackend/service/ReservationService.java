package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.dto.reservation.*;
import com.app.diamondhotelbackend.dto.shoppingcart.RoomTypeInfoDto;
import com.app.diamondhotelbackend.entity.*;
import com.app.diamondhotelbackend.exception.CheckInOutFormatException;
import com.app.diamondhotelbackend.exception.NotAllSelectedRoomsAvailableException;
import com.app.diamondhotelbackend.exception.ReservationNotFoundException;
import com.app.diamondhotelbackend.exception.UserProfileNotFoundException;
import com.app.diamondhotelbackend.repository.ReservationRepository;
import com.app.diamondhotelbackend.util.Constant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final TransactionService transactionService;

    private final DateService dateService;

    private final UserProfileService userProfileService;

    private final RoomService roomService;

    private final FlightService flightService;

    private final RoomTypeService roomTypeService;

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

    public UserReservationDetailsInfoResponseDto getUserReservationDetailsInfo(UserReservationDetailsInfoRequestDto userReservationDetailsInfoRequestDto) throws ReservationNotFoundException {
        Optional<Reservation> reservation = reservationRepository.findReservationByIdAndUserProfileId(userReservationDetailsInfoRequestDto.getReservationId(), userReservationDetailsInfoRequestDto.getUserProfileId());
        if (reservation.isEmpty()) {
            throw new ReservationNotFoundException("Reservation not found exception");
        }

        return UserReservationDetailsInfoResponseDto.builder()
                .checkIn(reservation.get().getCheckIn().toString())
                .checkOut(reservation.get().getCheckOut().toString())
                .roomCost(reservation.get().getRoomCost())
                .flightNumber(reservation.get().getFlight().getFlightNumber())
                .roomTypeDto(roomTypeService.toRoomTypeDtoMapper(reservation.get().getRoom().getRoomType()))
                .roomDto(roomService.toRoomDtoMapper(reservation.get().getRoom()))
                .transactionDto(transactionService.toTransactionDtoMapper(reservation.get().getTransaction()))
                .build();
    }

    public UserReservationNewResponseDto createNewReservation(UserReservationNewRequestDto userReservationNewRequestDto) throws CheckInOutFormatException, NotAllSelectedRoomsAvailableException, UserProfileNotFoundException {
        Optional<LocalDateTime> checkIn = dateService.isValidCheckInOrCheckOut(userReservationNewRequestDto.getCheckIn());
        Optional<LocalDateTime> checkOut = dateService.isValidCheckInOrCheckOut(userReservationNewRequestDto.getCheckOut());
        if (checkIn.isEmpty() || checkOut.isEmpty()) {
            throw new CheckInOutFormatException(Constant.INCORRECT_CHECK_IN_OR_CHECK_OUT_FORMAT);
        }
        if (roomService.isMismatchBetweenSelectedAndAvailableRooms(userReservationNewRequestDto.getRoomTypeInfoDtoList(), checkIn.get(), checkOut.get())) {
            throw new NotAllSelectedRoomsAvailableException(Constant.NUMBER_OF_AVAILABLE_ROOMS_HAS_CHANGED);
        }

        UserProfile userProfile = userProfileService.getUserProfileById(userReservationNewRequestDto.getUserProfileId());
        Flight flight = flightService.getOrCreateFlightByFlightNumber(userReservationNewRequestDto.getFlightNumber());
        Transaction transaction = transactionService.createNewTransaction(userReservationNewRequestDto.getCost());
        createAndSaveReservationList(userReservationNewRequestDto.getRoomTypeInfoDtoList(), checkIn.get(), checkOut.get(), userProfile, flight, transaction);

        return createUserReservationNewResponseDto(transaction);
    }

    private UserReservationNewResponseDto createUserReservationNewResponseDto(Transaction transaction) {
        return UserReservationNewResponseDto.builder()
                .status(Constant.WAITING_FOR_PAYMENT)
                .transactionCode(transaction.getCode())
                .reservationCost(transactionService.getTotalCost(transaction))
                .build();
    }

    private void createAndSaveReservationList(List<RoomTypeInfoDto> roomTypeInfoDtoList, LocalDateTime checkIn, LocalDateTime checkOut, UserProfile userProfile, Flight flight, Transaction transaction) {
        List<Reservation> reservationList = new ArrayList<>();
        roomTypeInfoDtoList.forEach(roomTypeInfoDto -> reservationList.addAll(bookRoom(roomTypeInfoDto, checkIn, checkOut)));
        reservationList.forEach(reservation -> {
            reservation.setUserProfile(userProfile);
            reservation.setFlight(flight);
            reservation.setTransaction(transaction);
        });

        reservationRepository.saveAll(reservationList);
    }

    private List<Reservation> bookRoom(RoomTypeInfoDto roomTypeInfoDto, LocalDateTime checkIn, LocalDateTime checkOut) {
        List<Room> roomList = roomService.getRoomListByRoomTypeNameCheckInAndCheckOut(roomTypeInfoDto.getRoomTypeName(), checkIn, checkOut);
        List<Reservation> reservationList = new ArrayList<>();
        for (int i = 0; i < roomTypeInfoDto.getSelectedRooms(); i++) {
            reservationList.add(Reservation.builder()
                    .room(roomList.get(i))
                    .checkIn(checkIn)
                    .checkOut(checkOut)
                    .roomCost(new BigDecimal(dateService.getDuration(checkIn, checkOut) * roomList.get(i).getRoomType().getPricePerHotelNight().longValue()))
                    .build()
            );
        }

        return reservationList;
    }

    public UserReservationCancellationResponseDto deleteReservationDetails(long reservationId) {
        Optional<Reservation> reservation = reservationRepository.findReservationById(reservationId);
        if (reservation.isEmpty()) {
            throw new ReservationNotFoundException("Reservation not found");
        }

        reservationRepository.delete(reservation.get());
        transactionService.updateTransactionAfterReservationCancellation(reservation.get());

        return UserReservationCancellationResponseDto
                .builder()
                .reservationId(reservationId)
                .status(Constant.CANCELLED)
                .build();
    }

    private UserReservationInfoDto toUserReservationInfoDtoMapper(Reservation reservation) {
        return UserReservationInfoDto.builder()
                .id(reservation.getId())
                .transactionCode(reservation.getTransaction().getCode())
                .roomType(reservation.getRoom().getRoomType().getName())
                .checkIn(reservation.getCheckIn().toString())
                .checkOut(reservation.getCheckOut().toString())
                .capacity(reservation.getRoom().getRoomType().getCapacity())
                .roomCost(reservation.getRoomCost())
                .build();
    }

    private List<UserReservationInfoDto> filterUserReservationInfoDtoListByCheckIn(List<UserReservationInfoDto> userReservationInfoDtoList, String checkIn) {
        Optional<LocalDateTime> checkInUser = dateService.isValidCheckInOrCheckOut(checkIn);
        if (checkInUser.isEmpty()) {
            return userReservationInfoDtoList;
        }

        return userReservationInfoDtoList
                .stream()
                .filter(userReservationInfoDto -> {
                    Optional<LocalDateTime> checkInReservation = dateService.isValidCheckInOrCheckOut(userReservationInfoDto.getCheckIn());
                    return checkInReservation.isPresent() && checkInReservation.get().isAfter(checkInUser.get());
                })
                .toList();
    }

    private List<UserReservationInfoDto> filterUserReservationInfoDtoListByCheckOut(List<UserReservationInfoDto> userReservationInfoDtoList, String checkOut) {
        Optional<LocalDateTime> checkOutUser = dateService.isValidCheckInOrCheckOut(checkOut);
        if (checkOutUser.isEmpty()) {
            return userReservationInfoDtoList;
        }

        return userReservationInfoDtoList
                .stream()
                .filter(userReservationInfoDto -> {
                    Optional<LocalDateTime> checkOutReservation = dateService.isValidCheckInOrCheckOut(userReservationInfoDto.getCheckIn());
                    return checkOutReservation.isPresent() && checkOutReservation.get().isBefore(checkOutUser.get());
                })
                .toList();
    }

    private List<UserReservationInfoDto> filterUserReservationInfoDtoListByRoomTypeName(List<UserReservationInfoDto> userReservationInfoDtoList, String roomTypeName) {
        Optional<String> roomTypeNameUser = roomTypeService.isValidRoomTypeName(roomTypeName);
        if (roomTypeNameUser.isEmpty()) {
            return userReservationInfoDtoList;
        }

        return userReservationInfoDtoList
                .stream()
                .filter(userReservationInfoDto -> roomTypeNameUser.get().equals(userReservationInfoDto.getRoomType()))
                .toList();
    }

    private List<UserReservationInfoDto> filterUserReservationInfoDtoListByCapacity(List<UserReservationInfoDto> userReservationInfoDtoList, String capacity) {
        Optional<Integer> capacityUser = roomTypeService.isValidCapacity(capacity);
        if (capacityUser.isEmpty()) {
            return userReservationInfoDtoList;
        }

        return userReservationInfoDtoList
                .stream()
                .filter(userReservationInfoDto -> capacityUser.get().equals(userReservationInfoDto.getCapacity()))
                .toList();
    }
}
