package com.example.cccchat.websocket;

import com.example.cccchat.entity.ChatMessage;
import com.example.cccchat.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
public class CustomWebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, Set<WebSocketSession>> roomSessions = new HashMap<>();

    @Autowired
    private ChatMessageRepository chatMessageRepository;


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("✅ WebSocket 연결 성공: {}", session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> jsonMap = objectMapper.readValue(message.getPayload(), Map.class);

        String type = jsonMap.get("type");
        String roomId = jsonMap.get("roomId");

        if ("join".equals(type)) {
            String username = jsonMap.get("username");

            roomSessions.putIfAbsent(roomId, new HashSet<>());
            roomSessions.get(roomId).add(session);
            log.info("📢 {}님이 {} 방에 입장함. 현재 세션 수: {}", username, roomId, roomSessions.get(roomId).size());

            Map<String, String> joinMessage = Map.of(
                    "type", "join",
                    "roomId", roomId,
                    "username", username
            );
            String jsonMessage = objectMapper.writeValueAsString(joinMessage);
            broadcastMessage(roomId, jsonMessage);
        }
    }

    @Scheduled(fixedRate = 5000) // 5초마다 실행
    public void autoDeleteMessages() throws IOException {
        LocalDateTime tenSecondsAgo = LocalDateTime.now().minusSeconds(10);
        List<ChatMessage> expiredMessages = chatMessageRepository.findByTimestampBefore(tenSecondsAgo);

        for (ChatMessage message : expiredMessages) {
            String deleteMessage = new ObjectMapper().writeValueAsString(
                    Map.of("type", "delete", "messageId", message.getId().toString())
            );

            broadcastMessage(message.getRoomId(), deleteMessage);
        }

        if (!expiredMessages.isEmpty()) {
            chatMessageRepository.deleteAll(expiredMessages);
            log.info("🚀 10초가 지난 메시지를 자동 삭제 완료!");
        }
    }

    public void broadcastMessage(String roomId, String message) throws IOException {
        if (roomSessions.containsKey(roomId)) {
            log.info("📨 {} 방에 메시지 전송 시작: {}", roomId, message);  // ✅ 방별 메시지 전송 로그 추가

            for (WebSocketSession session : roomSessions.get(roomId)) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                    log.info("📩 메시지 전송 완료 - 세션 ID: {} (채팅방: {})", session.getId(), roomId);
                }
            }
        } else {
            log.warn("⚠️ {} 방에 세션이 없습니다! 메시지를 보낼 수 없습니다.", roomId);
        }
    }
}
