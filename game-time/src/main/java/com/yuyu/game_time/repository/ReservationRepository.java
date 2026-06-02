package com.yuyu.game_time.repository;

import com.yuyu.game_time.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDate(LocalDate date);
    List<Reservation> findByDateBetween(LocalDate start, LocalDate end);
    List<Reservation> findByUserId(Long userId);
}
