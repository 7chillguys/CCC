package com.example.cccchat.controller;

import com.example.cccchat.entity.FileEntity;
import com.example.cccchat.repository.FileRepository;
import com.example.cccchat.service.S3Service;
import com.example.cccchat.websocket.CustomWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
    private final CustomWebSocketHandler webSocketHandler;
    private final S3Service s3Service;
    private final FileRepository fileRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("sender") String sender) {
        try {
            // ✅ S3에 파일 업로드 후 URL 반환
            String fileUrl = s3Service.uploadFile(file);

            // ✅ WebSocket으로 이미지 URL 브로드캐스트
            webSocketHandler.broadcastFileMessage(sender, fileUrl);

            return ResponseEntity.ok(Collections.singletonMap("fileUrl", fileUrl));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
        }
    }


    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        try {
            // DB에서 해당 파일 조회
            FileEntity fileEntity = fileRepository.findByFileUrl(fileUrl)
                    .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없음: " + fileUrl));

            // S3에서 파일 삭제
            s3Service.deleteFile(fileEntity.getFileUrl());

            // DB에서 파일 정보 삭제
            fileRepository.delete(fileEntity);

            return ResponseEntity.ok("파일 삭제 성공: " + fileUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("파일 삭제 실패: " + e.getMessage());
        }
    }
}
