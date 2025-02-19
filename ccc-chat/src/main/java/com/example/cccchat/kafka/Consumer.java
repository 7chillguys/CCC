package com.example.cccchat.kafka;

import com.example.cccchat.websocket.CustomWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
public class Consumer {

    private KafkaConsumer<String, String> kafkaConsumer = null;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupID;

    @Value("${spring.kafka.consumer.key-deserializer}")
    private String keyDeserializer;

    @Value("${spring.kafka.consumer.value-deserializer}")
    private String valueDeserializer;

    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String offsetReset;

    @Autowired
    private CustomWebSocketHandler customWebSocketHandler;

    @PostConstruct
    public void build() {
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupID);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        kafkaConsumer = new KafkaConsumer<>(properties);
    }

    @KafkaListener(topics = "kafkaTest")
    public void consume(@Payload String payload) throws IOException {
        log.info("📩 Kafka에서 수신한 메시지: {}", payload);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> messageData = objectMapper.readValue(payload, Map.class);

        String roomId = messageData.get("roomId");
        String message = messageData.get("message");

        if (roomId == null || message == null) {
            log.warn("🚨 잘못된 Kafka 메시지 수신 - roomId 또는 message 없음");
            return;
        }

        log.info("📢 WebSocket으로 메시지 전달 중... (roomId: {})", roomId);
        customWebSocketHandler.broadcastMessage(roomId, payload);
    }
}
