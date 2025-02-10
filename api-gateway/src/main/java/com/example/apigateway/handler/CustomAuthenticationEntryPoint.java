package com.example.apigateway.handler;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 401 인증 오류, 토큰 없이(허가 없이) 특정 페이지 요청 하면 발생
 */
@Component
public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {
    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        return Mono.fromRunnable( ()->{
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED); // "401 Unauthoriozed"
        });
    }
}
