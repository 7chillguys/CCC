package org.example.cccgame.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.cccgame.dto.roulette.ResultDto;
import org.example.cccgame.kafka.roulette.RouletteResultProducer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class RouletteService {
    private final RouletteResultProducer rouletteResultProducer;

    public RouletteService(RouletteResultProducer rouletteResultProducer) {
        this.rouletteResultProducer = rouletteResultProducer;
    }

    public List<ResultDto> startRoulette(List<String> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("룰렛의 항목이 비어 있습니다.");
        }

        Random random = new Random();
        int randomIndex = random.nextInt(items.size()); // 랜덤으로 선택
        String result = items.get(randomIndex);

        ResultDto resultDto = new ResultDto(result);
        List<ResultDto> results = new ArrayList<>();
        results.add(resultDto);

        try {
            rouletteResultProducer.rouletteSendMsg("game.roulette.result", resultDto);
        } catch (JsonProcessingException e) {
            System.out.println("Kafka 메시지 전송 실패: " + e.getMessage());
        }

        return results;
    }
}
