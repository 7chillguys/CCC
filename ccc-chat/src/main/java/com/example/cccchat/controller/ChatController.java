package com.example.cccchat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final RestTemplate restTemplate;

    public ChatController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
}
