package org.example.cccgame.kafka.roulette;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cccgame.dto.roulette.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RouletteResultConsumer {
    @Autowired
    private ObjectMapper objectMapper;
    
    @KafkaListener(topics = "game.roulette.result", groupId = "game-group")
    public void consumerRouletteResult(String message) {
        try {
            // JSON 문자열을 ResultDto 객체로 변환
            ResultDto resultDto = objectMapper.readValue(message, ResultDto.class);
            System.out.println("카프카 [룰렛게임] 결과 : " + resultDto.getResult());
        } catch (JsonProcessingException e) {
            System.err.println("JSON 파싱 오류: " + e.getMessage());
        }
    }
}
