package org.example.cccuser.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

/**
 * 게이트웨이에서 모드 걸러낸 요청만 진입 -> 모든 요청 통과
 */
//@Configuration
//@EnableWebFluxSecurity
//public class SecurityConfig {
//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//        http
//            // CSRF 공격에 대한 보호 설정 -> 비활성 처리
//            .csrf(ServerHttpSecurity.CsrfSpec::disable)
//            // 인증 없이 접근 가능한 페이지 (회원가입, 로그인, 기타 서비스별 별도 페이지들 가능)
//            .authorizeExchange( auth ->
//                    auth.anyExchange().permitAll() )
//            // 모든 요청은 무조건 통과 permitAll() 처리 예정
//            // 로그인 화면 비활성
//            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
//                ;
//        return http.build();
//    }
//
//    // 1. 암호화처리 -> 비번 처리 (필요시, 추가적 특정 필드 사용 가능함(마스킹 정보들)
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}

// MVC 서비스로 변경
@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.anyRequest().permitAll() // 모든 요청을 허용
                )
                .formLogin(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
