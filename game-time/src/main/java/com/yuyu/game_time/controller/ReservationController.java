package com.yuyu.game_time.controller;

import com.yuyu.game_time.dto.ReservationRequest;
import com.yuyu.game_time.entity.Reservation;
import com.yuyu.game_time.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/api/reservations")
    public ResponseEntity<?> create(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                    @RequestHeader(value = "X-Username", required = false) String username,
                                    @Valid @RequestBody ReservationRequest req) {
        if (userId == null || username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(com.yuyu.game_time.dto.ApiResponse.fail("缺少用户身份（X-User-Id / X-Username）", 401));
        }
        Reservation r = reservationService.create(userId, username, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(com.yuyu.game_time.dto.ApiResponse.ok(com.yuyu.game_time.dto.ReservationResponse.from(r)));
    }

    @GetMapping("/api/schedule/day")
    public ResponseEntity<?> day(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Reservation> items = reservationService.findByDate(date);
        List<com.yuyu.game_time.dto.ReservationResponse> resp = items.stream().map(com.yuyu.game_time.dto.ReservationResponse::from).toList();
        return ResponseEntity.ok(com.yuyu.game_time.dto.ApiResponse.ok(resp));
    }

    @GetMapping("/api/schedule")
    public ResponseEntity<?> week(@RequestParam("weekStart") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        LocalDate end = weekStart.plusDays(6);
        List<Reservation> items = reservationService.findBetween(weekStart, end);
        List<com.yuyu.game_time.dto.ReservationResponse> resp = items.stream().map(com.yuyu.game_time.dto.ReservationResponse::from).toList();
        return ResponseEntity.ok(com.yuyu.game_time.dto.ApiResponse.ok(resp));
    }

    @GetMapping("/api/reservations/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Reservation r = reservationService.findById(id);
        return ResponseEntity.ok(com.yuyu.game_time.dto.ApiResponse.ok(com.yuyu.game_time.dto.ReservationResponse.from(r)));
    }

    @DeleteMapping("/api/reservations/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                    @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(com.yuyu.game_time.dto.ApiResponse.fail("缺少用户身份", 401));
        try {
            reservationService.delete(id, userId);
            return ResponseEntity.ok(com.yuyu.game_time.dto.ApiResponse.okMsg("删除成功", null));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(com.yuyu.game_time.dto.ApiResponse.fail(ex.getMessage(), 403));
        }
    }

    @PatchMapping("/api/reservations/{id}")
    public ResponseEntity<?> patch(@PathVariable Long id,
                                   @RequestHeader(value = "X-User-Id", required = false) Long userId,
                                   @RequestBody ReservationRequest req) {
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(com.yuyu.game_time.dto.ApiResponse.fail("缺少用户身份", 401));
        try {
            Reservation updated = reservationService.updateNote(id, req.getNote(), userId);
            return ResponseEntity.ok(com.yuyu.game_time.dto.ApiResponse.ok(com.yuyu.game_time.dto.ReservationResponse.from(updated)));
        } catch (SecurityException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(com.yuyu.game_time.dto.ApiResponse.fail(ex.getMessage(), 403));
        }
    }
}
