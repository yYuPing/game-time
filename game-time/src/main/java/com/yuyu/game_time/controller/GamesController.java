package com.yuyu.game_time.controller;

import com.yuyu.game_time.dto.GameDto;
import com.yuyu.game_time.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GamesController {

    private final GameService gameService;

    public GamesController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<?> list() {
        List<GameDto> items = gameService.listAll();
        return ResponseEntity.ok(com.yuyu.game_time.dto.ApiResponse.ok(items));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        GameDto dto = gameService.findById(id);
        return ResponseEntity.ok(com.yuyu.game_time.dto.ApiResponse.ok(dto));
    }
}
