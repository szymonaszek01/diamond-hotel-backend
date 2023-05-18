package com.app.diamondhotelbackend.initializer;

import com.app.diamondhotelbackend.entity.*;
import com.app.diamondhotelbackend.repository.*;
import com.app.diamondhotelbackend.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
public class Initializer implements CommandLineRunner {

    private final RoomTypeRepository roomTypeRepository;

    private final FlightRepository flightRepository;

    private final RoomRepository roomRepository;

    private final UserProfileRepository userProfileRepository;

    private final RoomTypeOpinionRepository roomTypeOpinionRepository;

    private final ReservationRepository reservationRepository;

    private final List<RoomType> roomTypeList = new ArrayList<>();

    private final List<Flight> flightList = new ArrayList<>();

    private final List<Room> roomList = new ArrayList<>();

    private final List<UserProfile> userProfileList = new ArrayList<>();

    private final List<RoomTypeOpinion> roomTypeOpinionList = new ArrayList<>();

    private final List<Reservation> reservationList = new ArrayList<>();

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        initializeRoomTypeList();
        initializeFlightList();
        initializeRoomList();
        initializeUserProfileList();
        initializeRoomTypeOpinionList();
        initializeReservationList();

        roomTypeRepository.saveAll(roomTypeList);
        flightRepository.saveAll(flightList);
        roomRepository.saveAll(roomList);
        userProfileRepository.saveAll(userProfileList);
        roomTypeOpinionRepository.saveAll(roomTypeOpinionList);
        reservationRepository.saveAll(reservationList);
    }

    private void initializeRoomTypeList() {
        roomTypeList.addAll(Arrays.asList(
                RoomType.builder()
                        .name("Deluxe Suite")
                        .capacity(2)
                        .pricePerHotelNight(BigDecimal.valueOf(350))
                        .image("https://publish.purewow.net/wp-content/uploads/sites/2/2019/08/grand-velas.jpeg?fit=1360%2C906")
                        .equipmentList(Arrays.asList("King size bed", "Sofa bed", "Personalized Climate Control", "Minibar", "Balcony"))
                        .build(),
                RoomType.builder()
                        .name("Family Room")
                        .capacity(4)
                        .pricePerHotelNight(BigDecimal.valueOf(200))
                        .image("https://images.unsplash.com/photo-1578683010236-d716f9a3f461?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8bHV4dXJ5JTIwYmVkcm9vbXxlbnwwfHwwfHw%3D&w=1000&q=80")
                        .equipmentList(Arrays.asList("2 queen size beds", "Coffee maker", "Mini fridge", "Bathtub", "Personalized Climate Control"))
                        .build(),
                RoomType.builder()
                        .name("Standard Room")
                        .capacity(2)
                        .pricePerHotelNight(BigDecimal.valueOf(120))
                        .image("https://64.media.tumblr.com/52fd5697912a153efc4fdf057d877ab4/453e1459b5398164-9a/s1280x1920/e6917139b223f2ae9996ed37c11e422f94c316e8.jpg")
                        .equipmentList(Arrays.asList("King bed", "Desk", "Wardrobe", "Automated Lighting System", "Personalized Climate Control"))
                        .build()
        ));
    }

    private void initializeFlightList() {
        flightList.addAll(Arrays.asList(
                Flight.builder()
                        .arrival(LocalDateTime.of(2023, 6, 1, 13, 30))
                        .flyingFrom("New York")
                        .flyingTo("Malé")
                        .flightNumber("AA123")
                        .build(),
                Flight.builder()
                        .arrival(LocalDateTime.of(2023, 6, 1, 16, 45))
                        .flyingFrom("San Francisco")
                        .flyingTo("Malé")
                        .flightNumber("UA456")
                        .build(),
                Flight.builder()
                        .arrival(LocalDateTime.of(2023, 6, 2, 8, 15))
                        .flyingFrom("Paris")
                        .flyingTo("Malé")
                        .flightNumber("AF789")
                        .build(),
                Flight.builder()
                        .arrival(LocalDateTime.of(2023, 6, 3, 21, 10))
                        .flyingFrom("Tokyo")
                        .flyingTo("Malé")
                        .flightNumber("JL101")
                        .build(),
                Flight.builder()
                        .arrival(LocalDateTime.of(2023, 6, 4, 11, 20))
                        .flyingFrom("London")
                        .flyingTo("Malé")
                        .flightNumber("BA234")
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
                        role(Constant.USER).
                        build(),
                UserProfile.builder().
                        email("szymonCzopek@gmail.com").
                        password(passwordEncoder.encode("#Test456")).
                        firstname("Szymon").
                        lastname("Czopek").
                        age(18).
                        country("Poland").
                        passportNumber("01201201201").
                        phoneNumber("607244054").
                        city("Warszawa").
                        street("Dymińskiego 12/4").
                        postalCode("05-804").
                        role(Constant.USER).
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
                        role(Constant.USER).
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
                        .role(Constant.USER)
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
                        .role(Constant.ADMIN)
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
                        .room(roomList.get(0))
                        .userProfile(userProfileList.get(0))
                        .flight(flightList.get(0))
                        .checkIn(LocalDateTime.parse("2023-05-17T15:00:00"))
                        .checkOut(LocalDateTime.parse("2023-05-22T17:00:00"))
                        .numberOfAdults(2)
                        .numberOfChildren(1)
                        .cost(new BigDecimal("1250.00"))
                        .status(Constant.WAITING_FOR_PAYMENT)
                        .build(),
                Reservation.builder()
                        .room(roomList.get(1))
                        .userProfile(userProfileList.get(1))
                        .flight(flightList.get(1))
                        .checkIn(LocalDateTime.parse("2023-07-15T15:00:00"))
                        .checkOut(LocalDateTime.parse("2023-07-19T17:00:00"))
                        .numberOfAdults(1)
                        .numberOfChildren(0)
                        .cost(new BigDecimal("800.00"))
                        .status(Constant.WAITING_FOR_PAYMENT)
                        .build(),
                Reservation.builder()
                        .room(roomList.get(2))
                        .userProfile(userProfileList.get(2))
                        .flight(flightList.get(2))
                        .checkIn(LocalDateTime.parse("2023-08-10T15:00:00"))
                        .checkOut(LocalDateTime.parse("2023-08-14T17:00:00"))
                        .numberOfAdults(2)
                        .numberOfChildren(2)
                        .cost(new BigDecimal("2300.00"))
                        .status(Constant.CANCELLED)
                        .build(),
                Reservation.builder()
                        .room(roomList.get(3))
                        .userProfile(userProfileList.get(3))
                        .flight(flightList.get(3))
                        .checkIn(LocalDateTime.parse("2023-09-20T15:00:00"))
                        .checkOut(LocalDateTime.parse("2023-09-25T17:00:00"))
                        .numberOfAdults(2)
                        .numberOfChildren(0)
                        .cost(new BigDecimal("1500.00"))
                        .status(Constant.APPROVED)
                        .build(),
                Reservation.builder()
                        .room(roomList.get(4))
                        .userProfile(userProfileList.get(3))
                        .flight(flightList.get(4))
                        .checkIn(LocalDateTime.parse("2023-11-02T15:00:00"))
                        .checkOut(LocalDateTime.parse("2023-11-07T17:00:00"))
                        .numberOfAdults(3)
                        .numberOfChildren(1)
                        .cost(new BigDecimal("3500.00"))
                        .status(Constant.APPROVED)
                        .build()
        ));
    }
}
