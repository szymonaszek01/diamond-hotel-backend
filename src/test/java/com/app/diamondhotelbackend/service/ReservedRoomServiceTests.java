package com.app.diamondhotelbackend.service;

import com.app.diamondhotelbackend.entity.ReservedRoom;
import com.app.diamondhotelbackend.repository.ReservedRoomRepository;
import com.app.diamondhotelbackend.service.reservedroom.ReservedRoomServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservedRoomServiceTests {

    @InjectMocks
    private ReservedRoomServiceImpl reservedRoomService;

    @Mock
    private ReservedRoomRepository reservedRoomRepository;

    private List<ReservedRoom> reservedRoomList;

    @BeforeEach
    public void init() {
        reservedRoomList = List.of(
                ReservedRoom.builder()
                        .id(1)
                        .build(),
                ReservedRoom.builder()
                        .id(2)
                        .build()
        );
    }

    @Test
    public void ReservedRoomService_GetReservedRoomListByReservationCheckInAndReservationCheckOut_ReturnsReservedRoomList() {
        when(reservedRoomRepository.findAllByReservationCheckInAndReservationCheckOut(Mockito.any(Date.class), Mockito.any(Date.class))).thenReturn(reservedRoomList);

        List<ReservedRoom> foundReservedRoomList = reservedRoomService.getReservedRoomListByReservationCheckInAndReservationCheckOut(Date.valueOf("2023-09-20"), Date.valueOf("2023-09-25"));

        Assertions.assertThat(foundReservedRoomList).isNotNull();
        Assertions.assertThat(foundReservedRoomList.size()).isEqualTo(2);
    }
}
