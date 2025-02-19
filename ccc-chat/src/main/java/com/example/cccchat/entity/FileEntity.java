package com.example.cccchat.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;  // 업로드한 사용자 이메일

    private String fileUrl; // S3 URL

    public FileEntity(String sender, String fileUrl) {
        this.sender = sender;
        this.fileUrl = fileUrl;
    }
}