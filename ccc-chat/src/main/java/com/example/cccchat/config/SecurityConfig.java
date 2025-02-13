package com.example.cccchat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // ✅ CSRF 보호 비활성화 (WebSocket 및 정적 파일 요청 문제 해결)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**", "/chat/room", "/auth/login").permitAll()  // ✅ 주요 경로 허용
                        .requestMatchers("/ws/chat").permitAll()  // ✅ WebSocket 허용
                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // ✅ 세션 사용 안 함
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()) // ✅ X-Frame-Options 문제 해결
                        .httpStrictTransportSecurity(hsts -> hsts.disable()) // ✅ HTTPS 강제 적용 방지
                );

        return http.build();
    }
}
