package org.example.cccgame.controller;

import org.example.cccgame.dto.roulette.RequestDto;
import org.example.cccgame.dto.roulette.ResultDto;
import org.example.cccgame.service.RouletteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/game/roulette")
public class RouletteController {
    private final RouletteService rouletteService;

    public RouletteController(RouletteService rouletteService) {
        this.rouletteService = rouletteService;
    }

    @PostMapping("/start")
    public ResponseEntity<List<ResultDto>> startRoulette(@RequestBody RequestDto requestDto) {
        List<ResultDto> results = rouletteService.startRoulette(requestDto.getItems());
        return ResponseEntity.ok(results);
    }
}
