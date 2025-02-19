package com.example.cccchat.repository;

import com.example.cccchat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByRoomId(String roomId);

    List<ChatMessage> findByTimestampBefore(LocalDateTime timestamp);

    void deleteByTimestampBefore(LocalDateTime timestamp);
}
