package org.example.cccgame.controller;

import org.example.cccgame.dto.roulette.RequestDto;
import org.example.cccgame.dto.roulette.ResultDto;

import org.example.cccgame.service.RouletteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game/roulette")
public class RouletteController {
    @Autowired
    private  RouletteService rouletteService;

    @PostMapping("/start")
    public ResponseEntity<ResultDto> startRoulette(@RequestBody RequestDto requestDto) {
        String result = rouletteService.startRoulette(requestDto.getItems());
        return ResponseEntity.ok(new ResultDto("룰렛 결과: " + result));
    }
}
