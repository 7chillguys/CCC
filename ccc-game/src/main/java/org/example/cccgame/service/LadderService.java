package org.example.cccgame.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.cccgame.dto.ladder.ResultDto;
import org.example.cccgame.kafka.ladder.LadderResultProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class LadderService {
    @Autowired
    private LadderResultProducer ladderResultProducer;

    public List<ResultDto> startGame(List<String> players) {
        LadderGame game = new LadderGame(players, new Random().nextInt(6) + 5);
        List<ResultDto> results = new ArrayList<>();

        for (String player : players) {
            String result = game.getResult(player);
            ResultDto resultDto = new ResultDto(player, result);
            results.add(resultDto);

            try {
                ladderResultProducer.ladderSendMsg("game.ladder.result", resultDto);
            } catch (JsonProcessingException e) {
                System.out.println("Kafka 메시지 전송 실패: " + e.getMessage());
            }
        }

        return results;
    }

    public static class LadderGame {
        private final int height;
        private final int width;
        private final List<List<Boolean>> ladder;
        private final int failPosition;
        private final List<String> players;

        public LadderGame(List<String> players, int height) {
            this.height = height;
            this.width = players.size() - 1;
            this.ladder = new ArrayList<>();
            this.players = players;
            this.failPosition = new Random().nextInt(players.size());

            makeLadder();
        }

        private void makeLadder() {
            Random random = new Random();
            for (int i = 0; i < height; i++) {
                List<Boolean> row = new ArrayList<>();
                for (int j = 0; j < width; j++) {
                    boolean hasLeft = (j > 0) && row.get(j - 1);
                    row.add(!hasLeft && random.nextBoolean());
                }
                ladder.add(row);
            }
        }

        public String getResult(String playerName) {
            int startPosition = players.indexOf(playerName);
            if(startPosition == -1){
                return "플레이어를 찾을 수 없습니다.";
            }
            int position = startPosition;
            for(int i = 0; i < height; i++){
                if (position > 0 && ladder.get(i).get(position - 1)) {
                    position--;
                } else if(position < width && ladder.get(i).get(position)) {
                    position++;
                }
            }
            return position == failPosition ? "꽝" : "통과!";
        }
    }
}
