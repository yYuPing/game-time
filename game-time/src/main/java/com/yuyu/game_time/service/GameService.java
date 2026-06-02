package com.yuyu.game_time.service;

import com.yuyu.game_time.dto.GameDto;
import com.yuyu.game_time.entity.Game;
import com.yuyu.game_time.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<GameDto> listAll() {
        return gameRepository.findAll().stream()
                .map(g -> new GameDto(g.getId(), g.getName(), g.getColor(), g.getMeta()))
                .collect(Collectors.toList());
    }

    public GameDto findById(Long id) {
        Game g = gameRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("游戏未找到"));
        return new GameDto(g.getId(), g.getName(), g.getColor(), g.getMeta());
    }
}
