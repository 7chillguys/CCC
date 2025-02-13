package org.example.cccgame.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class LadderService {
    public LadderGame startGame(List<String> players){
        int randomHeight = new Random().nextInt(6) + 5; // 5 ~ 10 사이의 랜덤 값
        return new LadderGame(players, randomHeight);
    }

    public String getResult(LadderGame game, String playerName ){
        return game.getResult(playerName);
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
