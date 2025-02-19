package com.example.cccchat.controller;

import com.example.cccchat.entity.ChatRoom;
import com.example.cccchat.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat/room")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @PostMapping("/create")
    public ResponseEntity<ChatRoom> createRoom(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(chatRoomService.createRoom(request.get("name"), request.get("email"))); // ✅ email 추가
    }


    @GetMapping("/list")
    public ResponseEntity<List<ChatRoom>> getUserRooms(@RequestParam String email) {
        return ResponseEntity.ok(chatRoomService.getRoomsForUser(email));
    }

    @PostMapping("/invite")
    public ResponseEntity<String> inviteUser(@RequestBody Map<String, String> request) {
        String roomId = request.get("roomId");
        String email = request.get("email");

        if (roomId == null || email == null) {
            return ResponseEntity.badRequest().body("잘못된 요청");
        }

        boolean success = chatRoomService.inviteUser(roomId, email);
        if (success) {
            return ResponseEntity.ok("사용자가 초대되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("초대 실패");
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteRoom(@RequestParam String roomId) {
        boolean success = chatRoomService.deleteRoom(roomId);
        return success ? ResponseEntity.ok("삭제 성공") : ResponseEntity.badRequest().body("삭제 실패");
    }

    @DeleteMapping("/leave/{roomId}")
    public ResponseEntity<String> leaveRoom(@PathVariable String roomId, @RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("이메일이 제공되지 않았습니다.");
        }

        boolean success = chatRoomService.leaveRoom(roomId, email);
        return success ? ResponseEntity.ok("채팅방을 나갔습니다.") : ResponseEntity.badRequest().body("채팅방 나가기 실패");
    }

}