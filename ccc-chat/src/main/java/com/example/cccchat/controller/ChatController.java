package com.example.cccchat.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.cccchat.websocket.CustomWebSocketHandler;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final RestTemplate restTemplate;
    private final CustomWebSocketHandler webSocketHandler;

    public ChatController(RestTemplate restTemplate, CustomWebSocketHandler webSocketHandler) {
        this.restTemplate = restTemplate;
        this.webSocketHandler = webSocketHandler;
    }

    @GetMapping("/")
    public String index() {
        return "error";
    }

    @GetMapping("/room")
    public String chattingRoom(@RequestParam(value = "email", required = false) String email, Model model) {
        if (email == null) {
            model.addAttribute("name", "Guest");
        } else {
            model.addAttribute("name", email);
        }
        return "chattingRoom2";
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {

            String uploadDir = "uploads/";
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs(); // 폴더 없으면 생성
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);
            file.transferTo(filePath.toFile());

            String fileUrl = "/uploads/" + fileName;


            webSocketHandler.broadcastMessage("📎 파일 업로드됨: " + fileUrl);

            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
        }
    }
}
