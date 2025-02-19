package com.example.cccchat.controller;

import com.example.cccchat.entity.ChatMessage;
import com.example.cccchat.service.ChatMessageService;
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
    private final ChatMessageService chatService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ChatController(CustomWebSocketHandler webSocketHandler, ChatMessageService chatService) {
        this.webSocketHandler = webSocketHandler;
        this.chatService = chatService;
    }


    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, String> messageData) {
        String sender = messageData.get("sender");
        String message = messageData.get("message");
        String roomId = messageData.get("roomId");

        if (sender == null || message == null || roomId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "잘못된 요청"));
        }

        try {
            ChatMessage savedMessage = chatService.sendMessage(roomId, sender, message);

            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("id", savedMessage.getId());
            jsonMap.put("sender", sender);
            jsonMap.put("message", message);
            jsonMap.put("timestamp", savedMessage.getTimestamp().toString());

            String jsonMessage = new ObjectMapper().writeValueAsString(jsonMap);
            webSocketHandler.broadcastMessage(roomId, jsonMessage);

            return ResponseEntity.ok(jsonMap);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", "메시지 전송 실패"));
        }
    }

    @GetMapping("/check/{messageId}")
    public ResponseEntity<Map<String, Boolean>> checkMessageDeleted(@PathVariable Long messageId) {
        boolean exists = chatService.existsById(messageId);
        return ResponseEntity.ok(Map.of("deleted", !exists));
    }


    @DeleteMapping("/delete/{messageId}")
    public ResponseEntity<String> deleteMessage(@PathVariable Long messageId) {
        if (chatService.deleteMessage(messageId)) {
            return ResponseEntity.ok("메시지 삭제 성공");
        } else {
            return ResponseEntity.status(404).body("메시지를 찾을 수 없음");
        }
    }
}