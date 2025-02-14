package org.example.cccgame.kafka.roulette;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cccgame.dto.roulette.ResultDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RouletteResultProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    // 문자열 직렬, 역직렬 처리용도
    @Autowired
    private ObjectMapper objectMapper;

    public void rouletteSendMsg(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    public void rouletteSendMsg(String topic, ResultDto resultDto) throws JsonProcessingException {
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(resultDto));
    }
}