package com.app.diamondhotelbackend.controller;

import com.app.diamondhotelbackend.dto.statistics.request.StatisticsRequestDto;
import com.app.diamondhotelbackend.dto.statistics.response.StatisticsResponseDto;
import com.app.diamondhotelbackend.exception.ReservationProcessingException;
import com.app.diamondhotelbackend.service.statistics.StatisticsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequestMapping("/api/v1/statistics")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "https://diamond-hotel-frontend.vercel.app/"}, allowCredentials = "true")
public class StatisticsController {

    private final StatisticsServiceImpl statisticsService;

    @GetMapping("/year")
    public List<Integer> getYearStatistics() {
        return statisticsService.getYearStatistics();
    }

    @GetMapping("/month")
    public List<String> getMonthStatistics() {
        return statisticsService.getMonthStatistics();
    }

    @GetMapping("/summary")
    public ResponseEntity<StatisticsResponseDto> getSummaryStatistics(@RequestParam(value = "year") int year, @RequestParam(value = "month", defaultValue = "0", required = false) int month) {
        try {
            return ResponseEntity.ok(statisticsService.getSummaryStatistics(new StatisticsRequestDto(year, month)));
        } catch (ReservationProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/room-type")
    public ResponseEntity<StatisticsResponseDto> getRoomTypeStatistics(@RequestParam(value = "year") int year, @RequestParam(value = "month", defaultValue = "0", required = false) int month) {
        try {
            return ResponseEntity.ok(statisticsService.getRoomTypeStatistics(new StatisticsRequestDto(year, month)));
        } catch (ReservationProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/user-profile")
    public ResponseEntity<StatisticsResponseDto> getUserProfileStatistics(@RequestParam(value = "year") int year, @RequestParam(value = "month", defaultValue = "0", required = false) int month) {
        try {
            return ResponseEntity.ok(statisticsService.getUserProfileStatistics(new StatisticsRequestDto(year, month)));
        } catch (ReservationProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/reservation")
    public ResponseEntity<StatisticsResponseDto> getReservationStatistics(@RequestParam(value = "year") int year, @RequestParam(value = "month", defaultValue = "0", required = false) int month) {
        try {
            return ResponseEntity.ok(statisticsService.getReservationStatistics(new StatisticsRequestDto(year, month)));
        } catch (ReservationProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/reserved-room")
    public ResponseEntity<StatisticsResponseDto> getReservedRoomStatistics(@RequestParam(value = "year") int year, @RequestParam(value = "month", defaultValue = "0", required = false) int month) {
        try {
            return ResponseEntity.ok(statisticsService.getReservedRoomStatistics(new StatisticsRequestDto(year, month)));
        } catch (ReservationProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/income")
    public ResponseEntity<StatisticsResponseDto> getIncomeStatistics(@RequestParam(value = "year") int year, @RequestParam(value = "month", defaultValue = "0", required = false) int month) {
        try {
            return ResponseEntity.ok(statisticsService.getIncomeStatistics(new StatisticsRequestDto(year, month)));
        } catch (ReservationProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
