package org.example.cccgame.controller;

import org.example.cccgame.dto.ladder.RequestDto;
import org.example.cccgame.dto.ladder.ResultDto;
import org.example.cccgame.kafka.ladder.LadderResultProducer;
import org.example.cccgame.service.LadderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/game/ladder")
public class LadderController {
    private final LadderService ladderService;

    public LadderController(LadderService ladderService) {
        this.ladderService = ladderService;
    }

    @PostMapping("/start")
    public ResponseEntity<List<ResultDto>> startGame(@RequestBody RequestDto requestDto) {
        List<ResultDto> results = ladderService.startGame(requestDto.getPlayers());
        return ResponseEntity.ok(results);
    }
}
