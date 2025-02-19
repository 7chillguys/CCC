package com.example.cccchat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @ElementCollection
    private Set<String> members = new HashSet<>();

    @Builder
    public ChatRoom(String name, String creatorEmail) {
        this.name = name;
        this.members.add(creatorEmail);
    }

    public void addMember(String email) {
        members.add(email);
    }
}
