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

        String payload = message.getPayload();

        // ✅ 파일 업로드 메시지 감지
        if (payload.startsWith("FILE_UPLOAD:")) {
            String fileUrl = payload.replace("FILE_UPLOAD:", "").trim();
            broadcastFileMessage(fileUrl);
        } else {
            // 일반 텍스트 메시지 전송
            broadcastMessage(payload);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        log.info("🔌 WebSocket 연결 종료: {}", session.getId());
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

    //  파일 업로드 메시지 브로드캐스트
    public void broadcastFileMessage(String fileUrl) throws IOException {
        log.info("📢 파일 업로드 브로드캐스트: {}", fileUrl);

        String fileMessage = "<a href='" + fileUrl + "' target='_blank'><img src='" + fileUrl + "' style='max-width: 200px; max-height: 200px; border-radius: 5px;'/></a>";

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(fileMessage));
            }
        }
    }
}
