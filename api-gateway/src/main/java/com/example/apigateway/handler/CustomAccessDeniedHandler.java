package com.example.apigateway.handler;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 권한이 없는 유저가 사용자 요청을 할 경우 (관리자 메뉴를 일반 유저가 접근)
 * 엑세스 거부 403 AccessDenied
 */
@Component
public class CustomAccessDeniedHandler implements ServerAccessDeniedHandler {
    // 재정의
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        // 자바의 쓰레드 생성 방법중 Runnable 객체 생성및 쓰레드 가동
        // Runnable 객체를 비동기적으로 생성하는 코드
        return Mono.fromRunnable( ()->{
            // 응답 객체를 횏득하여 응답 코드 세팅
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        });
    }
}
