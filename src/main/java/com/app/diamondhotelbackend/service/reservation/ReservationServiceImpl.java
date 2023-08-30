package com.app.diamondhotelbackend.service.reservation;

import com.app.diamondhotelbackend.dto.reservation.*;
import com.app.diamondhotelbackend.dto.shoppingcart.RoomTypeInfoDto;
import com.app.diamondhotelbackend.entity.*;
import com.app.diamondhotelbackend.exception.CheckInOutFormatException;
import com.app.diamondhotelbackend.exception.NotAllSelectedRoomsAvailableException;
import com.app.diamondhotelbackend.exception.ReservationNotFoundException;
import com.app.diamondhotelbackend.exception.UserProfileProcessingException;
import com.app.diamondhotelbackend.repository.ReservationRepository;
import com.app.diamondhotelbackend.service.room.RoomServiceImpl;
import com.app.diamondhotelbackend.service.roomtype.RoomTypeServiceImpl;
import com.app.diamondhotelbackend.service.transaction.TransactionServiceImpl;
import com.app.diamondhotelbackend.service.userprofile.UserProfileServiceImpl;
import com.app.diamondhotelbackend.service.flight.FlightServiceImpl;
import com.app.diamondhotelbackend.util.ConstantUtil;
import com.app.diamondhotelbackend.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    private final TransactionServiceImpl transactionService;

    private final UserProfileServiceImpl userProfileService;

    private final RoomServiceImpl roomService;

    private final FlightServiceImpl flightService;

    private final RoomTypeServiceImpl roomTypeService;

    @Override
    public UserReservationAllResponseDto getUserReservationInfoList(UserReservationAllRequestDto userReservationAllRequestDto) {
        long userProfileId = userReservationAllRequestDto.getUserProfileId();
        List<Reservation> reservationList = userProfileService.isAdmin(userProfileId) ? getReservationListForAdmin() : getReservationListForUser(userProfileId);
        List<UserReservationInfoDto> userReservationInfoDtoList = reservationList
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

    @Override
    public UserReservationDetailsInfoResponseDto getUserReservationDetailsInfo(UserReservationDetailsInfoRequestDto userReservationDetailsInfoRequestDto) throws ReservationNotFoundException {
        Optional<Reservation> reservation = userProfileService.isAdmin(userReservationDetailsInfoRequestDto.getUserProfileId()) ? reservationRepository.findReservationById(userReservationDetailsInfoRequestDto.getReservationId()) :
                reservationRepository.findReservationByIdAndUserProfileId(userReservationDetailsInfoRequestDto.getReservationId(), userReservationDetailsInfoRequestDto.getUserProfileId());
        if (reservation.isEmpty()) {
            throw new ReservationNotFoundException("Reservation not found exception");
        }

        return UserReservationDetailsInfoResponseDto.builder()
                .checkIn(reservation.get().getCheckIn().toString())
                .checkOut(reservation.get().getCheckOut().toString())
                .roomCost(reservation.get().getRoomCost())
                .flightNumber(reservation.get().getFlight().getFlightNumber())
                .email(reservation.get().getUserProfile().getEmail())
                .roomTypeDto(roomTypeService.toRoomTypeDtoMapper(reservation.get().getRoom().getRoomType()))
                .roomDto(roomService.toRoomDtoMapper(reservation.get().getRoom()))
                .transactionDto(transactionService.toTransactionDtoMapper(reservation.get().getTransaction()))
                .build();
    }

    @Override
    public UserReservationNewResponseDto createNewReservation(UserReservationNewRequestDto userReservationNewRequestDto) throws CheckInOutFormatException, NotAllSelectedRoomsAvailableException, UserProfileProcessingException {
        Optional<LocalDateTime> checkIn = DateUtil.isValidCheckInOrCheckOut(userReservationNewRequestDto.getCheckIn());
        Optional<LocalDateTime> checkOut = DateUtil.isValidCheckInOrCheckOut(userReservationNewRequestDto.getCheckOut());
        if (checkIn.isEmpty() || checkOut.isEmpty()) {
            throw new CheckInOutFormatException(ConstantUtil.INCORRECT_CHECK_IN_OR_CHECK_OUT_FORMAT_EXCEPTION);
        }
        if (roomService.isMismatchBetweenSelectedAndAvailableRooms(userReservationNewRequestDto.getRoomTypeInfoDtoList(), checkIn.get(), checkOut.get())) {
            throw new NotAllSelectedRoomsAvailableException(ConstantUtil.NUMBER_OF_AVAILABLE_ROOMS_HAS_CHANGED_EXCEPTION);
        }

        UserProfile userProfile = userProfileService.getUserProfileById(userReservationNewRequestDto.getUserProfileId());
        Flight flight = flightService.getOrCreateFlightByFlightNumber(userReservationNewRequestDto.getFlightNumber());
        Transaction transaction = transactionService.createNewTransaction(userReservationNewRequestDto.getCost());
        createAndSaveReservationList(userReservationNewRequestDto.getRoomTypeInfoDtoList(), checkIn.get(), checkOut.get(), userProfile, flight, transaction);

        return createUserReservationNewResponseDto(transaction);
    }

    @Override
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
                .status(ConstantUtil.CANCELLED)
                .build();
    }

    private List<Reservation> getReservationListForAdmin() {
        return reservationRepository.findAll();
    }

    private List<Reservation> getReservationListForUser(long userProfileId) {
        return reservationRepository.findAllByUserProfileId(userProfileId)
                .stream()
                .filter(reservation -> !ConstantUtil.CANCELLED.equals(reservation.getTransaction().getStatus()))
                .collect(Collectors.toList());
    }

    private UserReservationNewResponseDto createUserReservationNewResponseDto(Transaction transaction) {
        return UserReservationNewResponseDto.builder()
                .status(ConstantUtil.WAITING_FOR_PAYMENT)
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
                    .roomCost(new BigDecimal(DateUtil.getDuration(checkIn, checkOut) * roomList.get(i).getRoomType().getPricePerHotelNight().longValue()))
                    .build()
            );
        }

        return reservationList;
    }

    private UserReservationInfoDto toUserReservationInfoDtoMapper(Reservation reservation) {
        return UserReservationInfoDto.builder()
                .id(reservation.getId())
                .transactionCode(reservation.getTransaction().getCode())
                .roomType(reservation.getRoom().getRoomType().getName())
                .checkIn(reservation.getCheckIn().toString())
                .checkOut(reservation.getCheckOut().toString())
                .capacity(reservation.getRoom().getRoomType().getCapacity())
                .roomNumber(reservation.getRoom().getNumber())
                .roomFloor(reservation.getRoom().getFloor())
                .email(reservation.getUserProfile().getEmail())
                .build();
    }

    private List<UserReservationInfoDto> filterUserReservationInfoDtoListByCheckIn(List<UserReservationInfoDto> userReservationInfoDtoList, String checkIn) {
        Optional<LocalDateTime> checkInUser = DateUtil.isValidCheckInOrCheckOut(checkIn);
        if (checkInUser.isEmpty()) {
            return userReservationInfoDtoList;
        }

        return userReservationInfoDtoList
                .stream()
                .filter(userReservationInfoDto -> {
                    Optional<LocalDateTime> checkInReservation = DateUtil.isValidCheckInOrCheckOut(userReservationInfoDto.getCheckIn());
                    return checkInReservation.isPresent() && checkInReservation.get().isAfter(checkInUser.get());
                })
                .toList();
    }

    private List<UserReservationInfoDto> filterUserReservationInfoDtoListByCheckOut(List<UserReservationInfoDto> userReservationInfoDtoList, String checkOut) {
        Optional<LocalDateTime> checkOutUser = DateUtil.isValidCheckInOrCheckOut(checkOut);
        if (checkOutUser.isEmpty()) {
            return userReservationInfoDtoList;
        }

        return userReservationInfoDtoList
                .stream()
                .filter(userReservationInfoDto -> {
                    Optional<LocalDateTime> checkOutReservation = DateUtil.isValidCheckInOrCheckOut(userReservationInfoDto.getCheckIn());
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
