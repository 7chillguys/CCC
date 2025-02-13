package com.example.apigateway.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Component
public class JwtFilter implements WebFilter, ApplicationContextAware {
    private final JwtTokenProvider jwtTokenProvider;
    private ApplicationContext applicationContext;
    private final RedisTemplate<String, String> redisTemplate;

    private final String[] FREE_PATHS = {
            "/**", "/auth/login", "/user/signup", "/user/valid", "/ws/chat"
    };

    public JwtFilter(JwtTokenProvider jwtTokenProvider, RedisTemplate<String, String> redisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String reqUrl = exchange.getRequest().getURI().getPath();

        // ✅ WebSocket 요청은 필터링 없이 통과 (중복 로그 방지)
        if (reqUrl.startsWith("/ws/chat")) {
            if (!exchange.getResponse().isCommitted()) {
                System.out.println("📌 WebSocket 요청 감지: " + reqUrl);
            }
            return chain.filter(exchange);
        }

        // 기존 인증 로직 유지
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        System.out.println("요청 헤더에서 토큰 획득: " + token);

        if (token != null) {
            try {
                String email = jwtTokenProvider.getEmailFromToken(token);
                System.out.println("✅ 토큰에서 추출한 이메일: " + email);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        new User(email, "", new ArrayList<>()), null, null
                );

                return chain.filter(
                        exchange.mutate().request(
                                exchange.getRequest().mutate().header("X-Auth-User", email).build()
                        ).build()
                ).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));

            } catch (ExpiredJwtException e) {
                System.out.println("🚨 만료된 JWT 토큰");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            } catch (Exception e) {
                System.out.println("🚨 토큰 검증 실패");
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }
}
