package com.app.diamondhotelbackend.initializer;

import com.app.diamondhotelbackend.entity.*;
import com.app.diamondhotelbackend.repository.*;
import com.app.diamondhotelbackend.util.ConstantUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
public class Initializer implements CommandLineRunner {

    private final RoomTypeRepository roomTypeRepository;

    private final FlightRepository flightRepository;

    private final TransactionRepository transactionRepository;

    private final RoomRepository roomRepository;

    private final UserProfileRepository userProfileRepository;

    private final RoomTypeOpinionRepository roomTypeOpinionRepository;

    private final ReservationRepository reservationRepository;

    private final ReservedRoomRepository reservedRoomRepository;

    private final List<RoomType> roomTypeList = new ArrayList<>();

    private final List<Flight> flightList = new ArrayList<>();

    private final List<Transaction> transactionList = new ArrayList<>();

    private final List<Room> roomList = new ArrayList<>();

    private final List<UserProfile> userProfileList = new ArrayList<>();

    private final List<RoomTypeOpinion> roomTypeOpinionList = new ArrayList<>();

    private final List<Reservation> reservationList = new ArrayList<>();

    private final List<ReservedRoom> reservedRoomList = new ArrayList<>();

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        initializeRoomTypeList();
        initializeFlightList();
        initializeTransactionList();
        initializeRoomList();
        initializeUserProfileList();
        initializeRoomTypeOpinionList();
        initializeReservationList();
        initializeReservedRoomList();

        roomTypeRepository.saveAll(roomTypeList);
        flightRepository.saveAll(flightList);
        transactionRepository.saveAll(transactionList);
        roomRepository.saveAll(roomList);
        userProfileRepository.saveAll(userProfileList);
        roomTypeOpinionRepository.saveAll(roomTypeOpinionList);
        reservationRepository.saveAll(reservationList);
        reservedRoomRepository.saveAll(reservedRoomList);
    }

    private void initializeRoomTypeList() {
        roomTypeList.addAll(Arrays.asList(
                RoomType.builder()
                        .name("Deluxe Suite")
                        .adults(2)
                        .children(0)
                        .pricePerHotelNight(BigDecimal.valueOf(350))
                        .image("https://publish.purewow.net/wp-content/uploads/sites/2/2019/08/grand-velas.jpeg?fit=1360%2C906")
                        .equipment(Arrays.asList("King size bed", "Sofa bed", "Personalized Climate Control", "Minibar", "Balcony"))
                        .build(),
                RoomType.builder()
                        .name("Family Room")
                        .adults(2)
                        .children(2)
                        .pricePerHotelNight(BigDecimal.valueOf(200))
                        .image("https://images.unsplash.com/photo-1578683010236-d716f9a3f461?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8bHV4dXJ5JTIwYmVkcm9vbXxlbnwwfHwwfHw%3D&w=1000&q=80")
                        .equipment(Arrays.asList("2 queen size beds", "Coffee maker", "Mini fridge", "Bathtub", "Personalized Climate Control"))
                        .build(),
                RoomType.builder()
                        .name("Standard Room")
                        .adults(2)
                        .children(0)
                        .pricePerHotelNight(BigDecimal.valueOf(120))
                        .image("https://64.media.tumblr.com/52fd5697912a153efc4fdf057d877ab4/453e1459b5398164-9a/s1280x1920/e6917139b223f2ae9996ed37c11e422f94c316e8.jpg")
                        .equipment(Arrays.asList("King bed", "Desk", "Wardrobe", "Automated Lighting System", "Personalized Climate Control"))
                        .build()
        ));
    }

    private void initializeFlightList() {
        flightList.addAll(Arrays.asList(
                Flight.builder()
                        .flightNumber("VB123")
                        .build(),
                Flight.builder()
                        .flightNumber("UA456")
                        .build(),
                Flight.builder()
                        .flightNumber("AF789")
                        .build(),
                Flight.builder()
                        .flightNumber("JL101")
                        .build(),
                Flight.builder()
                        .flightNumber("BA234")
                        .build()
        ));
    }

    private void initializeTransactionList() {
        transactionList.addAll(Arrays.asList(
                Transaction.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .token("token1")
                        .cost(new BigDecimal(1800))
                        .tax(new BigDecimal(180))
                        .status(ConstantUtil.APPROVED)
                        .build(),
                Transaction.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .token("token2")
                        .cost(new BigDecimal(1500))
                        .tax(new BigDecimal(150))
                        .status(ConstantUtil.APPROVED)
                        .build(),
                Transaction.builder()
                        .createdAt(new Date(System.currentTimeMillis()))
                        .expiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 15))
                        .token("token3")
                        .cost(new BigDecimal(1200))
                        .tax(new BigDecimal(120))
                        .status(ConstantUtil.APPROVED)
                        .build()
        ));
    }

    private void initializeRoomList() {
        roomList.addAll(Arrays.asList(
                Room.builder()
                        .number(101)
                        .floor(1)
                        .roomType(roomTypeList.get(0))
                        .build(),
                Room.builder()
                        .number(102)
                        .floor(1)
                        .roomType(roomTypeList.get(0))
                        .build(),
                Room.builder()
                        .number(201)
                        .floor(2)
                        .roomType(roomTypeList.get(1))
                        .build(),
                Room.builder()
                        .number(202)
                        .floor(2)
                        .roomType(roomTypeList.get(1))
                        .build(),
                Room.builder()
                        .number(301)
                        .floor(3)
                        .roomType(roomTypeList.get(1))
                        .build(),
                Room.builder()
                        .number(302)
                        .floor(3)
                        .roomType(roomTypeList.get(2))
                        .build()
        ));
    }

    private void initializeUserProfileList() {
        this.userProfileList.addAll(Arrays.asList(
                UserProfile.builder().
                        email("basiaGawron@gmail.com").
                        password(passwordEncoder.encode("#Test123")).
                        firstname("Basia").
                        lastname("Gawron").
                        age(18).
                        country("Poland").
                        passportNumber("01101101101").
                        phoneNumber("987234654").
                        city("Warszawa").
                        street("Tadeusza Rechniewskiego 13/4").
                        postalCode("09-404").
                        role(ConstantUtil.USER).
                        authProvider(ConstantUtil.LOCAL).
                        accountConfirmed(true).
                        build(),
                UserProfile.builder().
                        email("szymonCzopek@gmail.com").
                        firstname("Szymon").
                        lastname("Czopek").
                        picture(null).
                        role(ConstantUtil.USER).
                        authProvider(ConstantUtil.OAUTH2).
                        accountConfirmed(true).
                        build(),
                UserProfile.builder().
                        passportNumber("01301301301").
                        email("krzysiekKrzak@gmail.com").
                        password(passwordEncoder.encode("#Test789")).
                        firstname("Krzysiek").
                        lastname("Krzak").
                        age(18).
                        country("Poland").
                        passportNumber("01201200001").
                        phoneNumber("807211004").
                        city("Warszawa").
                        street("Bogusławskiego 1/4").
                        postalCode("09-304").
                        role(ConstantUtil.USER).
                        authProvider(ConstantUtil.LOCAL).
                        accountConfirmed(true).
                        build(),
                UserProfile.builder()
                        .email("robert.garcia@gmail.com")
                        .password(passwordEncoder.encode("#Test108"))
                        .firstname("Robert")
                        .lastname("Garcia")
                        .age(40)
                        .country("USA")
                        .passportNumber("FF353893321")
                        .phoneNumber("655578904")
                        .city("Miami")
                        .street("1212 Ocean Dr")
                        .postalCode("33-139")
                        .role(ConstantUtil.USER)
                        .authProvider(ConstantUtil.LOCAL)
                        .accountConfirmed(true)
                        .build(),
                UserProfile.builder()
                        .email("emily.davis@gmail.com")
                        .password(passwordEncoder.encode("#Test369"))
                        .firstname("Emily")
                        .lastname("Davis")
                        .age(23)
                        .country("USA")
                        .passportNumber("CF445401486")
                        .phoneNumber("789555345")
                        .city("Chicago")
                        .street("1414 W Jackson Blvd")
                        .postalCode("60-607")
                        .role(ConstantUtil.ADMIN)
                        .authProvider(ConstantUtil.LOCAL)
                        .accountConfirmed(true)
                        .build(),
                UserProfile.builder()
                        .email("szymon-jakubaszek@wp.pl")
                        .password(passwordEncoder.encode("#Test1910"))
                        .firstname("Szymon")
                        .lastname("Jakubaszek")
                        .age(22)
                        .country("Poland")
                        .passportNumber("CF445401499")
                        .phoneNumber("789500345")
                        .city("Garwolin")
                        .street("Adamska 12/23")
                        .postalCode("90-607")
                        .role(ConstantUtil.USER)
                        .authProvider(ConstantUtil.LOCAL)
                        .accountConfirmed(true)
                        .build()
        ));
    }

    private void initializeRoomTypeOpinionList() {
        this.roomTypeOpinionList.addAll(Arrays.asList(
                RoomTypeOpinion.builder()
                        .rate(8)
                        .roomType(roomTypeList.get(0))
                        .userProfile(userProfileList.get(0))
                        .build(),
                RoomTypeOpinion.builder()
                        .rate(9)
                        .roomType(roomTypeList.get(1))
                        .userProfile(userProfileList.get(1))
                        .build(),
                RoomTypeOpinion.builder()
                        .rate(10)
                        .roomType(roomTypeList.get(2))
                        .userProfile(userProfileList.get(2))
                        .build(),
                RoomTypeOpinion.builder()
                        .rate(7)
                        .roomType(roomTypeList.get(0))
                        .userProfile(userProfileList.get(0))
                        .build(),
                RoomTypeOpinion.builder()
                        .rate(8)
                        .roomType(roomTypeList.get(1))
                        .userProfile(userProfileList.get(1))
                        .build(),
                RoomTypeOpinion.builder()
                        .rate(6)
                        .roomType(roomTypeList.get(2))
                        .userProfile(userProfileList.get(2))
                        .build(),
                RoomTypeOpinion.builder()
                        .rate(10)
                        .roomType(roomTypeList.get(0))
                        .userProfile(userProfileList.get(0))
                        .build(),
                RoomTypeOpinion.builder()
                        .rate(5)
                        .roomType(roomTypeList.get(1))
                        .userProfile(userProfileList.get(1))
                        .build(),
                RoomTypeOpinion.builder()
                        .rate(8)
                        .roomType(roomTypeList.get(2))
                        .userProfile(userProfileList.get(2))
                        .build()
        ));
    }

    private void initializeReservationList() {
        reservationList.addAll(Arrays.asList(
                Reservation.builder()
                        .checkIn(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                        .checkOut(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                        .adults(2)
                        .children(2)
                        .userProfile(userProfileList.get(0))
                        .flight(flightList.get(0))
                        .transaction(transactionList.get(0))
                        .build(),
                Reservation.builder()
                        .checkIn(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                        .checkOut(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                        .adults(2)
                        .children(2)
                        .userProfile(userProfileList.get(1))
                        .flight(flightList.get(1))
                        .transaction(transactionList.get(1))
                        .build(),
                Reservation.builder()
                        .checkIn(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                        .checkOut(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                        .adults(2)
                        .children(2)
                        .userProfile(userProfileList.get(2))
                        .flight(flightList.get(2))
                        .transaction(transactionList.get(2))
                        .build()
        ));
    }

    private void initializeReservedRoomList() {
        reservedRoomList.addAll(Arrays.asList(
                ReservedRoom.builder()
                        .occupied(0)
                        .cost(new BigDecimal(roomList.get(0).getRoomType().getPricePerHotelNight().longValue() * 7))
                        .reservation(reservationList.get(0))
                        .room(roomList.get(0))
                        .build(),
                ReservedRoom.builder()
                        .occupied(0)
                        .cost(new BigDecimal(roomList.get(2).getRoomType().getPricePerHotelNight().longValue() * 7))
                        .reservation(reservationList.get(0))
                        .room(roomList.get(2))
                        .build(),
                ReservedRoom.builder()
                        .occupied(0)
                        .cost(new BigDecimal(roomList.get(1).getRoomType().getPricePerHotelNight().longValue() * 7))
                        .reservation(reservationList.get(1))
                        .room(roomList.get(1))
                        .build(),
                ReservedRoom.builder()
                        .occupied(0)
                        .cost(new BigDecimal(roomList.get(3).getRoomType().getPricePerHotelNight().longValue() * 7))
                        .reservation(reservationList.get(1))
                        .room(roomList.get(3))
                        .build(),
                ReservedRoom.builder()
                        .occupied(0)
                        .cost(new BigDecimal(roomList.get(4).getRoomType().getPricePerHotelNight().longValue() * 7))
                        .reservation(reservationList.get(2))
                        .room(roomList.get(4))
                        .build(),
                ReservedRoom.builder()
                        .occupied(0)
                        .cost(new BigDecimal(roomList.get(5).getRoomType().getPricePerHotelNight().longValue() * 7))
                        .reservation(reservationList.get(2))
                        .room(roomList.get(5))
                        .build()
        ));
    }
}
