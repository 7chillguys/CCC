package com.example.cccchat.service;

import com.example.cccchat.entity.ChatRoom;
import com.example.cccchat.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChatRoomService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public ChatRoom createRoom(String name, String creatorEmail) {
        ChatRoom room = new ChatRoom(name, creatorEmail);
        return chatRoomRepository.save(room);
    }


    public List<ChatRoom> getRoomsForUser(String email) {
        return chatRoomRepository.findByMembersContaining(email);
    }


    public boolean inviteUser(String roomId, String email) {
        Optional<ChatRoom> roomOptional = chatRoomRepository.findById(roomId);

        if (roomOptional.isPresent()) {
            ChatRoom room = roomOptional.get();
            room.addMember(email);  // ✅ 사용자를 채팅방에 추가
            chatRoomRepository.save(room);
            return true;
        }
        return false;
    }

    public boolean leaveRoom(String roomId, String email) {
        ChatRoom room = chatRoomRepository.findById(roomId).orElse(null);
        if (room == null) {
            return false;
        }

        room.getMembers().remove(email);

        if (room.getMembers().isEmpty()) {
            chatRoomRepository.delete(room);
        } else {
            chatRoomRepository.save(room);
        }

        return true;
    }


    public boolean deleteRoom(String roomId) {
        if (chatRoomRepository.existsById(roomId)) {
            chatRoomRepository.deleteById(roomId);
            return true;
        }
        return false;
    }
}
