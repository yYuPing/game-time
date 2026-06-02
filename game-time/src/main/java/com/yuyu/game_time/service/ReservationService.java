package com.yuyu.game_time.service;

import com.yuyu.game_time.dto.ReservationRequest;
import com.yuyu.game_time.entity.Game;
import com.yuyu.game_time.entity.Reservation;
import com.yuyu.game_time.mapper.GameMapper;
import com.yuyu.game_time.mapper.ReservationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationMapper reservationMapper;
    private final GameMapper gameMapper;

    public ReservationService(ReservationMapper reservationMapper, GameMapper gameMapper) {
        this.reservationMapper = reservationMapper;
        this.gameMapper = gameMapper;
    }

    @Transactional
    public Reservation create(Long userId, String username, ReservationRequest req) {
        Game game = gameMapper.findById(req.getGameId());
        if (game == null) {
            throw new IllegalArgumentException("游戏不存在");
        }
        Reservation r = new Reservation();
        r.setUserId(userId);
        r.setUsername(username);
        r.setDate(req.getDate());
        r.setTimeslot(req.getTimeslot());
        r.setGameId(game.getId());
        r.setGameName(game.getName());
        r.setNote(req.getNote());

        reservationMapper.insert(r);
        return r;
    }

    public List<Reservation> findByDate(LocalDate date) {
        return reservationMapper.findByDate(date);
    }

    public List<Reservation> findBetween(LocalDate start, LocalDate end) {
        return reservationMapper.findByDateBetween(start, end);
    }

    public Reservation findById(Long id) {
        Reservation r = reservationMapper.findById(id);
        if (r == null) {
            throw new IllegalArgumentException("预约未找到");
        }
        return r;
    }

    @Transactional
    public void delete(Long id, Long requesterId) {
        Reservation r = findById(id);
        if (!r.getUserId().equals(requesterId)) {
            throw new SecurityException("无权限删除");
        }
        reservationMapper.deleteById(id);
    }

    @Transactional
    public Reservation updateNote(Long id, String note, Long requesterId) {
        Reservation r = findById(id);
        if (!r.getUserId().equals(requesterId)) {
            throw new SecurityException("无权限修改");
        }
        reservationMapper.updateNote(id, note);
        r.setNote(note);
        return r;
    }
}
