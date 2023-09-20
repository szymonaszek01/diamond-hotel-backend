package com.app.diamondhotelbackend.repository;

import com.app.diamondhotelbackend.entity.*;
import com.app.diamondhotelbackend.util.ConstantUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
public class ReservedRoomRepositoryTests {

    @Autowired
    private ReservedRoomRepository reservedRoomRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private ReservedRoom reservedRoom;

    private List<ReservedRoom> reservedRoomList;

    @BeforeEach
    public void init() {
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
                        .build())
        );

        List<Reservation> reservationList = List.of(
                testEntityManager.persistAndFlush(Reservation.builder()
                        .checkIn(Date.valueOf("2023-09-20"))
                        .checkOut(Date.valueOf("2023-09-25"))
                        .payment(paymentList.get(0))
                        .build()),
                testEntityManager.persistAndFlush(Reservation.builder()
                        .checkIn(Date.valueOf("2023-12-20"))
                        .checkOut(Date.valueOf("2023-12-25"))
                        .payment(paymentList.get(1))
                        .build())
        );

        reservedRoom = ReservedRoom.builder()
                .room(savedRoomList.get(0))
                .reservation(reservationList.get(0))
                .build();

        reservedRoomList = List.of(
                ReservedRoom.builder()
                        .room(savedRoomList.get(0))
                        .reservation(reservationList.get(0))
                        .cost(BigDecimal.valueOf(5 * savedRoomList.get(0).getRoomType().getPricePerHotelNight().doubleValue()))
                        .build(),
                ReservedRoom.builder()
                        .room(savedRoomList.get(1))
                        .reservation(reservationList.get(1))
                        .cost(BigDecimal.valueOf(5 * savedRoomList.get(1).getRoomType().getPricePerHotelNight().doubleValue()))
                        .build()
        );
    }

    @Test
    public void ReservedRoomRepository_Save_ReturnsSavedReservedRoom() {
        ReservedRoom savedReservedRoom = reservedRoomRepository.save(reservedRoom);

        Assertions.assertThat(savedReservedRoom).isNotNull();
        Assertions.assertThat(savedReservedRoom.getId()).isGreaterThan(0);
    }

    @Test
    public void ReservedRoomRepository_FindAll_ReturnsReservedRoomList() {
        reservedRoomRepository.saveAll(reservedRoomList);
        List<ReservedRoom> foundReservedRoomList = reservedRoomRepository.findAll();

        Assertions.assertThat(foundReservedRoomList).isNotNull();
        Assertions.assertThat(foundReservedRoomList.size()).isEqualTo(2);
    }

    @Test
    public void ReservedRoomRepository_FindAllByReservationCheckInAndReservationCheckOut_ReturnsReservedRoomList() {
        reservedRoomRepository.saveAll(reservedRoomList);
        List<ReservedRoom> foundReservedRoomList = reservedRoomRepository.findAllByReservationCheckInAndReservationCheckOut(Date.valueOf("2023-09-20"), Date.valueOf("2023-09-25"));

        Assertions.assertThat(foundReservedRoomList).isNotNull();
        Assertions.assertThat(foundReservedRoomList.size()).isEqualTo(1);
    }

    @Test
    public void ReservedRoomRepository_FindAllByReservationId_ReturnsReservedRoomList() {
        reservedRoomRepository.saveAll(reservedRoomList);
        List<ReservedRoom> foundReservedRoomList = reservedRoomRepository.findAllByReservationId(1);

        Assertions.assertThat(foundReservedRoomList).isNotNull();
        Assertions.assertThat(foundReservedRoomList.size()).isEqualTo(1);
    }

    @Test
    public void ReservedRoomRepository_FindById_ReturnsOptionalReservedRoom() {
        ReservedRoom savedReservedRoom = reservedRoomRepository.save(reservedRoom);
        Optional<ReservedRoom> ReservedRoomOptional = reservedRoomRepository.findById((reservedRoom.getId()));

        Assertions.assertThat(ReservedRoomOptional).isPresent();
        Assertions.assertThat(ReservedRoomOptional.get().getId()).isEqualTo(savedReservedRoom.getId());
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
