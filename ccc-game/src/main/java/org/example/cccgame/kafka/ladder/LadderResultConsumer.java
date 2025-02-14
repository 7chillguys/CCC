package org.example.cccgame.kafka.ladder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cccgame.dto.ladder.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LadderResultConsumer {
    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "game.ladder.result", groupId = "game-group")
    public void consumerLadderResult(String message) {
        try {
            // JSON 문자열을 ResultDto 객체로 변환
            ResultDto resultDto = objectMapper.readValue(message, ResultDto.class);
            System.out.println("카프카 [사다리게임] 결과 : " + resultDto.getPlayerName() + " → " + resultDto.getResult());
        } catch (JsonProcessingException e) {
            System.err.println("JSON 파싱 오류: " + e.getMessage());
        }
    }
}
