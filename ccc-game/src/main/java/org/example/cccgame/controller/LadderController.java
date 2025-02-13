package org.example.cccgame.controller;

import org.example.cccgame.dto.ladder.RequestDto;
import org.example.cccgame.dto.ladder.ResultDto;
import org.example.cccgame.service.LadderService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private LadderService ladderService;

    @PostMapping("/start")
    public ResponseEntity<List<ResultDto>> startGame(@RequestBody RequestDto requestDto) {
        List<String> players = requestDto.getPlayers();

        LadderService.LadderGame game = ladderService.startGame(players);

        List<ResultDto> results = new ArrayList<>();
        for(String player : players) {
            String result = ladderService.getResult(game, player);
            results.add(new ResultDto(player, result));
        }
        return ResponseEntity.ok(results);
    }
}
