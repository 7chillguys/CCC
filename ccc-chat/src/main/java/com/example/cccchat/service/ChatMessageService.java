package com.example.cccchat.service;

import com.example.cccchat.entity.ChatMessage;
import com.example.cccchat.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public ChatMessage sendMessage(String roomId, String sender, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRoomId(roomId);
        chatMessage.setSender(sender);
        chatMessage.setMessage(message);
        chatMessage.setTimestamp(LocalDateTime.now());

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        System.out.println("✅ 저장된 메시지 ID: " + savedMessage.getId());
        return savedMessage;
    }

    public boolean deleteMessage(Long messageId) {
        Optional<ChatMessage> message = chatMessageRepository.findById(messageId);
        if (message.isPresent()) {
            chatMessageRepository.deleteById(messageId);
            return true;
        }
        return false; // 메시지가 이미 삭제되었을 경우 중복 요청 방지
    }

    public boolean existsById(Long messageId) {
        return chatMessageRepository.existsById(messageId);
    }

    @Scheduled(fixedRate = 5000)
    public void autoDeleteMessages() {
        LocalDateTime tenSecondsAgo = LocalDateTime.now().minusSeconds(9);
        List<ChatMessage> expiredMessages = chatMessageRepository.findByTimestampBefore(tenSecondsAgo);

        if (!expiredMessages.isEmpty()) {
            chatMessageRepository.deleteAll(expiredMessages);
            System.out.println("🚀 10초가 지난 메시지를 자동 삭제 완료!");
        }
    }
}
