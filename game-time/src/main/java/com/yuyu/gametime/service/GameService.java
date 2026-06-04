package com.yuyu.gametime.service;

import com.yuyu.gametime.dto.GameDto;
import com.yuyu.gametime.entity.Game;
import com.yuyu.gametime.mapper.GameMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final GameMapper gameMapper;

    public GameService(GameMapper gameMapper) {
        this.gameMapper = gameMapper;
    }

    public List<GameDto> listAll() {
        return gameMapper.findAll().stream()
                .map(g -> new GameDto(g.getId(), g.getName(), g.getColor(), g.getMeta()))
                .collect(Collectors.toList());
    }

    public GameDto findById(Long id) {
        Game g = gameMapper.findById(id);
        if (g == null) {
            throw new IllegalArgumentException("游戏未找到");
        }
        return new GameDto(g.getId(), g.getName(), g.getColor(), g.getMeta());
    }
}
