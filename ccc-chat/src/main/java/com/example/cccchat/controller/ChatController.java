package com.example.cccchat.controller;

import com.example.cccchat.websocket.CustomWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final CustomWebSocketHandler webSocketHandler;

    @Autowired
    public ChatController(CustomWebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @GetMapping("/room")
    public ResponseEntity<Map<String, String>> getChatRoom(@RequestParam("email") String email) {
        System.out.println("✅ 채팅방 입장 요청: " + email); // ✅ 요청 로그 추가
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "채팅방 입장 성공");
        response.put("email", email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody Map<String, String> messageData) {
        System.out.println("✅ 메시지 전송 요청: " + messageData.get("sender") + " - " + messageData.get("message")); // ✅ 요청 로그 추가

        String sender = messageData.get("sender");
        String message = messageData.get("message");

        if (sender == null || message == null) {
            return ResponseEntity.badRequest().body("잘못된 요청");
        }

        try {
            // ✅ JSON 형태로 변환
            Map<String, String> jsonMap = new HashMap<>();
            jsonMap.put("sender", sender);
            jsonMap.put("message", message);
            String jsonMessage = new ObjectMapper().writeValueAsString(jsonMap);

            webSocketHandler.broadcastMessage(jsonMessage);
            return ResponseEntity.ok("메시지 전송 성공");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("메시지 전송 실패");
        }
    }

}

