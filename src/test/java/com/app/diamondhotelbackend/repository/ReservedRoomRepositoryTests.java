package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.*;
import com.app.diamondhotelbackend.util.ConstantUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static com.app.diamondhotelbackend.specification.ReservedRoomSpecification.paymentStatusEqual;

@DataJpaTest
@ActiveProfiles("test")
public class ReservedRoomRepositoryTests {

    @Autowired
    private ReservedRoomRepository reservedRoomRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private ReservedRoom reservedRoom;

    private List<ReservedRoom> reservedRoomList;

    private PageRequest pageRequest;

    @BeforeEach
    public void init() {
        List<UserProfile> savedUserProfileList = List.of(
                testEntityManager.persistAndFlush(
                        UserProfile.builder()
                                .email("email1")
                                .passportNumber("passportNumber1")
                                .build()
                ),
                testEntityManager.persistAndFlush(
                        UserProfile.builder()
                                .email("email2")
                                .passportNumber("passportNumber2")
                                .build()
                )
        );

        List<RoomType> savedRoomTypeList = List.of(
                testEntityManager.persistAndFlush(RoomType.builder()
                        .name("Deluxe Suite")
                        .adults(2)
                        .children(0)
                        .pricePerHotelNight(BigDecimal.valueOf(350))
                        .build()),
                testEntityManager.persistAndFlush(RoomType.builder()
                        .name("Family ReservedRoom")
                        .adults(2)
                        .children(2)
                        .pricePerHotelNight(BigDecimal.valueOf(200))
                        .build())
        );

        List<Room> savedRoomList = List.of(
                testEntityManager.persistAndFlush(Room.builder()
                        .number(101)
                        .floor(1)
                        .roomType(savedRoomTypeList.get(0))
                        .build()),
                testEntityManager.persistAndFlush(Room.builder()
                        .number(102)
                        .floor(1)
                        .roomType(savedRoomTypeList.get(1))
                        .build())
        );

        List<Payment> paymentList = List.of(
                testEntityManager.persistAndFlush(Payment.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .token("token1")
                        .status(ConstantUtil.APPROVED)
                        .build()),
                testEntityManager.persistAndFlush(Payment.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .token("token2")
                        .status(ConstantUtil.APPROVED)
                        .build()),
                testEntityManager.persistAndFlush(Payment.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .token("token3")
                        .status(ConstantUtil.APPROVED)
                        .build()),
                testEntityManager.persistAndFlush(Payment.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .token("token4")
                        .status(ConstantUtil.APPROVED)
                        .build())
        );

        List<Reservation> savedReservationList = List.of(
                testEntityManager.persistAndFlush(Reservation.builder()
                        .checkIn(Date.valueOf("2023-09-20"))
                        .checkOut(Date.valueOf("2023-09-25"))
                        .payment(paymentList.get(0))
                        .userProfile(savedUserProfileList.get(0))
                        .build()),
                testEntityManager.persistAndFlush(Reservation.builder()
                        .checkIn(Date.valueOf("2023-10-20"))
                        .checkOut(Date.valueOf("2023-10-25"))
                        .payment(paymentList.get(1))
                        .userProfile(savedUserProfileList.get(0))
                        .build()),
                testEntityManager.persistAndFlush(Reservation.builder()
                        .checkIn(Date.valueOf("2023-11-20"))
                        .checkOut(Date.valueOf("2023-11-25"))
                        .payment(paymentList.get(2))
                        .userProfile(savedUserProfileList.get(1))
                        .build()),
                testEntityManager.persistAndFlush(Reservation.builder()
                        .checkIn(Date.valueOf("2023-12-20"))
                        .checkOut(Date.valueOf("2023-12-25"))
                        .payment(paymentList.get(3))
                        .userProfile(savedUserProfileList.get(1))
                        .build())
        );

        reservedRoom = ReservedRoom.builder()
                .room(savedRoomList.get(0))
                .reservation(savedReservationList.get(0))
                .cost(BigDecimal.valueOf(5 * savedRoomList.get(0).getRoomType().getPricePerHotelNight().doubleValue()))
                .build();

        reservedRoomList = List.of(
                ReservedRoom.builder()
                        .room(savedRoomList.get(0))
                        .reservation(savedReservationList.get(0))
                        .cost(BigDecimal.valueOf(5 * savedRoomList.get(0).getRoomType().getPricePerHotelNight().doubleValue()))
                        .build(),
                ReservedRoom.builder()
                        .room(savedRoomList.get(1))
                        .reservation(savedReservationList.get(1))
                        .cost(BigDecimal.valueOf(5 * savedRoomList.get(1).getRoomType().getPricePerHotelNight().doubleValue()))
                        .build(),
                ReservedRoom.builder()
                        .room(savedRoomList.get(0))
                        .reservation(savedReservationList.get(2))
                        .cost(BigDecimal.valueOf(5 * savedRoomList.get(0).getRoomType().getPricePerHotelNight().doubleValue()))
                        .build(),
                ReservedRoom.builder()
                        .room(savedRoomList.get(1))
                        .reservation(savedReservationList.get(3))
                        .cost(BigDecimal.valueOf(5 * savedRoomList.get(1).getRoomType().getPricePerHotelNight().doubleValue()))
                        .build()
        );

        pageRequest = PageRequest.of(0, 3);
    }

    @Test
    public void ReservedRoomRepository_Save_ReturnsSavedReservedRoom() {
        ReservedRoom savedReservedRoom = reservedRoomRepository.save(reservedRoom);

        Assertions.assertThat(savedReservedRoom).isNotNull();
        Assertions.assertThat(savedReservedRoom.getId()).isGreaterThan(0);
    }

    @Test
    public void ReservedRoomRepository_FindAll_ReturnsReservedRoomList() {
        Specification<ReservedRoom> reservationSpecification = Specification.where(paymentStatusEqual(ConstantUtil.WAITING_FOR_PAYMENT));
        reservedRoomRepository.saveAll(reservedRoomList);

        Page<ReservedRoom> foundReservedRoomPage = reservedRoomRepository.findAll(reservationSpecification, pageRequest);

        Assertions.assertThat(foundReservedRoomPage).isNotNull();
    }

    @Test
    public void ReservedRoomRepository_FindById_ReturnsOptionalReservedRoom() {
        ReservedRoom savedReservedRoom = reservedRoomRepository.save(reservedRoom);

        Optional<ReservedRoom> ReservedRoomOptional = reservedRoomRepository.findById((reservedRoom.getId()));

        Assertions.assertThat(ReservedRoomOptional).isPresent();
        Assertions.assertThat(ReservedRoomOptional.get().getId()).isEqualTo(savedReservedRoom.getId());
    }

    @Test
    public void ReservedRoomRepository_Count_ReturnsLong() {
        reservedRoomRepository.saveAll(reservedRoomList);

        Long countedReservedRoomList = reservedRoomRepository.count();

        Assertions.assertThat(countedReservedRoomList).isNotNull();
        Assertions.assertThat(countedReservedRoomList).isEqualTo(4);
    }

    @Test
    public void ReservedRoomRepository_Update_ReturnsReservedRoom() {
        ReservedRoom savedReservedRoom = reservedRoomRepository.save(reservedRoom);

        Optional<ReservedRoom> ReservedRoomOptional = reservedRoomRepository.findById((savedReservedRoom.getId()));

        Assertions.assertThat(ReservedRoomOptional).isPresent();
        Assertions.assertThat(ReservedRoomOptional.get().getId()).isEqualTo(savedReservedRoom.getId());

        ReservedRoomOptional.get().setOccupied(1);
        ReservedRoom updatedReservedRoom = reservedRoomRepository.save(ReservedRoomOptional.get());

        Assertions.assertThat(updatedReservedRoom).isNotNull();
        Assertions.assertThat(updatedReservedRoom.getOccupied()).isEqualTo(1);
    }

    @Test
    public void ReservedRoomRepository_Delete_ReturnsNothing() {
        ReservedRoom savedReservedRoom = reservedRoomRepository.save(reservedRoom);

        reservedRoomRepository.deleteById(savedReservedRoom.getId());

        Optional<ReservedRoom> ReservedRoomOptional = reservedRoomRepository.findById(reservedRoom.getId());

        Assertions.assertThat(ReservedRoomOptional).isEmpty();
    }
}
