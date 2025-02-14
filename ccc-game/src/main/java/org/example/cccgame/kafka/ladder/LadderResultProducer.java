package org.example.cccgame.kafka.ladder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cccgame.dto.ladder.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LadderResultProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    // 문자열 직렬, 역직렬 처리용도
    @Autowired
    private ObjectMapper objectMapper;

    public void ladderSendMsg(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    public void ladderSendMsg(String topic, ResultDto resultDto) throws JsonProcessingException {
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(resultDto));
    }
}
