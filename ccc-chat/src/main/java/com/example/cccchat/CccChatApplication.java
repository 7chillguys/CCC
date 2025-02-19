package com.example.cccchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CccChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(CccChatApplication.class, args);
    }

}
