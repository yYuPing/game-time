package com.yuyu.game_time.service;

import com.yuyu.game_time.dto.ReservationRequest;
import com.yuyu.game_time.entity.Game;
import com.yuyu.game_time.entity.Reservation;
import com.yuyu.game_time.repository.GameRepository;
import com.yuyu.game_time.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final GameRepository gameRepository;

    public ReservationService(ReservationRepository reservationRepository, GameRepository gameRepository) {
        this.reservationRepository = reservationRepository;
        this.gameRepository = gameRepository;
    }

    @Transactional
    public Reservation create(Long userId, String username, ReservationRequest req) {
        Game game = gameRepository.findById(req.getGameId()).orElseThrow(() -> new IllegalArgumentException("游戏不存在"));
        Reservation r = new Reservation();
        r.setUserId(userId);
        r.setUsername(username);
        r.setDate(req.getDate());
        r.setTimeslot(req.getTimeslot());
        r.setGameId(game.getId());
        r.setGameName(game.getName());
        r.setNote(req.getNote());
        return reservationRepository.save(r);
    }

    public List<Reservation> findByDate(LocalDate date) {
        return reservationRepository.findByDate(date);
    }

    public List<Reservation> findBetween(LocalDate start, LocalDate end) {
        return reservationRepository.findByDateBetween(start, end);
    }

    public Reservation findById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("预约未找到"));
    }

    @Transactional
    public void delete(Long id, Long requesterId) {
        Reservation r = findById(id);
        if (!r.getUserId().equals(requesterId)) {
            throw new SecurityException("无权限删除");
        }
        reservationRepository.deleteById(id);
    }

    @Transactional
    public Reservation updateNote(Long id, String note, Long requesterId) {
        Reservation r = findById(id);
        if (!r.getUserId().equals(requesterId)) {
            throw new SecurityException("无权限修改");
        }
        r.setNote(note);
        return reservationRepository.save(r);
    }
}
