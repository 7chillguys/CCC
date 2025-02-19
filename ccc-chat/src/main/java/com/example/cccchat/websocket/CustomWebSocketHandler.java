package com.example.cccchat.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
public class CustomWebSocketHandler extends TextWebSocketHandler {

    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("✅ WebSocket 연결 성공: {}", session.getId());

        // ✅ 중복 추가 방지: 이미 존재하는 세션이면 추가하지 않음
        if (!sessions.contains(session)) {
            sessions.add(session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        log.info("📩 메시지 수신: {}", message.getPayload());

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> jsonMap = objectMapper.readValue(message.getPayload(), Map.class);

        String type = jsonMap.get("type");

        // ✅ 사용자가 입장했을 경우 입장 메시지 브로드캐스트
        if ("join".equals(type)) {
            String username = jsonMap.get("username");
            String joinMessage = objectMapper.writeValueAsString(
                    Map.of("sender", "시스템", "message", username + "님이 입장했습니다.")
            );
            broadcastMessage(joinMessage);
        } else {
            String sender = jsonMap.get("sender");
            String msg = jsonMap.get("message");

            if (sender != null && msg != null) {
                String chatMessage = objectMapper.writeValueAsString(
                        Map.of("sender", sender, "message", msg)
                );
                broadcastMessage(chatMessage);
            }
        }
    }




    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("🔌 WebSocket 연결 종료: {}", session.getId());
        sessions.remove(session);
    }


    // ✅ 일반 메시지 브로드캐스트
    public void broadcastMessage(String message) throws IOException {
        log.info("📢 WebSocket으로 메시지 브로드캐스트: {}", message);

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        }
    }

    public void broadcastFileMessage(String sender, String fileUrl) throws IOException {
        log.info("📢 파일 업로드 브로드캐스트: {} by {}", fileUrl, sender);
        ObjectMapper objectMapper = new ObjectMapper();
        String fileMessage = objectMapper.writeValueAsString(
                Map.of("sender", sender, "fileUrl", fileUrl, "type", "file")
        );
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(fileMessage));
            }
        }
    }
}
