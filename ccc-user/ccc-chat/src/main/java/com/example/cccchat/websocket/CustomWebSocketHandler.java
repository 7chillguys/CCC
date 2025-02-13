package com.example.cccchat.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class CustomWebSocketHandler extends TextWebSocketHandler {

    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("✅ WebSocket 연결 성공: {}", session.getId());
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        log.info("📩 메시지 수신: {}", message.getPayload());

        // 받은 메시지를 모든 클라이언트에게 전송 (브로드캐스트)
        broadcastMessage(message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("🔌 WebSocket 연결 종료: {}", session.getId());
    }

    // ✅ Kafka에서 받은 메시지를 WebSocket으로 전송하는 기능 추가
    public void broadcastMessage(String message) throws IOException {
        log.info("📢 WebSocket으로 메시지 브로드캐스트: {}", message);

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        }
    }
}
