package com.example.apigateway.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
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

/**
 * 웹 요청시 토큰 관리, 스프링 컨텍스트 접근 고려 -> 인터페이스 2개 활용
 */
@Component
public class JwtFilter implements WebFilter, ApplicationContextAware {
    // 생성자 방식의 DI로 의존성 주입 처리
    private final JwtTokenProvider jwtTokenProvider; // 필터 내부에서 사용 목적
    private ApplicationContext applicationContext; // setApplicationContext 내부에서 사용
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    // 인증에 관련없는 URL 목록 정의
    private final String[] FREE_PATHS = {
            "/",
            "/auth/login",
            "/user/signup",
            "/user/vaild",
            // 향후 확대 가능함 -> 차후 스프링시큐리티에서 반영 -> 같이 적용되게 구성 고민
    };

    // 맴버가 많을 경우는 생성자 초기화 방식을 직접 구성
    public JwtFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 필요시 사용 (단, 레디스로 사용할것이라 미비할듯, 없을듯 => 예상)
        this.applicationContext = applicationContext;
    }

    /**
     *  HTTP 요청을 가로채서, 필터 체인에 적용된 루틴을 반영하여 필터링 처리
     *  - 요청 필터링 : 요청에 대한 검증, 로그, 인증, 권한 등 검사 수행 가능
     *  - 응답 필터링 : 응답을 가로채서 응답코드 변경 , 추가값 설정등 가능함
     *
     *  - 아래 과정이 간단하게 구성되기 위해서 -> JwtTokenProvider (토큰 관리, 공급, 검증등)
     *  - 서비스가 연결된 후 -> 회원가입 진행시 체크 가능함
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 요청-> 게이트웨이 -> 스프링시큐리티 점검 -> (*)필터팅 : 라우팅 or 401 에러 처리(서비스가 등록되면 달라질수 있음)-> 서비스진입
        System.out.println("JwtFilter filter() 호출, 요청 발생시 계속 호출");

        // 1. 요청 URL 확인 로그 출력
        // 요청 URL 획득
        String reqUrl = exchange.getRequest().getURI().getPath();
        System.out.println("요청 URL " + reqUrl );

        // 2. 스프링시큐리에서 인증 없이 통과 가능한 URL 들은 바로 통과 (체크 필요)->종료(요청을 넘김)
        //    인증없이 통과될 URL과 일치하는 URL 존재하는지 체킹
        AntPathMatcher matcher = new AntPathMatcher(); // 해당 객체를 통해서 순환(반복문) 점검 (도구)
        for(String path : FREE_PATHS) {
            if (matcher.match(path, reqUrl)) { // 매칭되면, 이하 과정 생략 -> 요청을 개별 서비스로 전달
                System.out.println("인증없이 통과 처리 " + reqUrl );
                return chain.filter(exchange);
            }
        }

        // 스프링시큐리티의 설정에 의해 인증 체크를 해야하는 요청만 도달하는 코드
        // 여기까지 도착한 요청에는 토큰 정보가 존재할것이다!! (전제하에 진행)
        // 3. 인증을 필요로 하는 요청만 도달 -> 요청 프로토콜의 헤더에서 토큰 추출
        String token = exchange.getRequest().getHeaders().getFirst("Authorization"); // 차후 상수값 확인
        System.out.println("요청 헤더에서 토큰 획득 : " + token);

        // 4. 토큰이 존재한다면
        if( token != null ){
            try{
                // 정상 처리
                // 4-1-1. 인증 절차 진행 (회원정보중에서 이메일(키로 사용) 집중적으로 체크)-> 토큰의 추가 정보에서 추출됨
                // 이메일 추출
                String email = jwtTokenProvider.getEmailFromToken(token); // 만료, 토큰 유효성 검사
                System.out.println("토큰에서 email 추출: " + email);
                // 4-1-2. 게이트웨이에서 인증 객체 생성 요청 헤더에 정보를 심어서 개별 서비스로 전달
                //        개별 서비스는 게이트웨이에서 심은 정보를 기반으로 인증여부를 판단할수 있다!! -> 확장성 가질수 있다
                //        개별 서비스는 게이트웨이의 시그널을 통해 인증을 간단하게 정리 or 토큰을 통해서 다시 검증가능
                // 적용 예시
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        new User(email, "", new ArrayList<>()), null, null
                );
                // 모든 요청은 게이트웨이를 통과 -> 필터링시 문제 없으면 반영 시킴 -> 하위 모든 서비스들은 이를 통해서 유효성 보장받음
                return chain.filter(
                        exchange.mutate().request(
                                exchange.getRequest()
                                        // 헤더에 특정 이름으로 이메일을 세팅
                                        // 헤더명이 X-....  => 커스텀 헤더명일 확률이 아주 높다
                                .mutate().header("X-Auth-User", email)
                                .build() )
                        .build())
                        // 인증정보 시큐리티컨텍스트에 설정
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));

            }catch ( ExpiredJwtException e ){
                // 회원가입->로그인 완료후 진행
                // 리플레시 토큰 활용
                // 4-2. 기간 만료 토큰 -> 리플레시 토큰을 통해서 엑세스 토큰 재발급 시나리오 진행
                // 1. 이메일 정보를 토큰으로부터 추출 (기간이 만료되었도 정보 추출 가능)
                String email = e.getClaims().get("email", String.class);
                // 2. 이메일을 이용 redis를 통해서 리플레시 토큰 획득 -> 로그인 진행시 엑세스/리플레시 토큰 발급(redis 저장예정)
                String refreshToken = redisTemplate.opsForValue().get(email);
                // 3. 리플레시 토큰의 유효성 검사, 존재여부 검사
                if (refreshToken != null && jwtTokenProvider.validateToken(refreshToken)) {
                    // 4. (엑세스) 토큰 발급
                    String newAccessToken = jwtTokenProvider.createToken(email);
                    // 5. 응답 헤더에 엑세스 토큰 세팅 -> 응답을 받은후 쿠키쪽에서 갱신 처리 됨
                    // 6. 위에서 진행했던 절차 반복 : 요청 헤더데 표식, 인증정보 시큐리티컨텍스트에 설정
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            new User(email, "", new ArrayList<>()), null, new ArrayList<>()
                    );
                    return chain.filter(exchange.mutate()
                                    .request(exchange.getRequest().mutate()
                                            .header("X-Authenticated-User", email)
                                            .header(HttpHeaders.AUTHORIZATION, newAccessToken)
                                            .build())
                                    .build())
                            // 인증 정보를 Spring Security의 SecurityContext에 설정
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
                }else {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                // 조작된 토큰, 토큰의 누락(부분손실)로 전달 => 노이즈발생
                throw new RuntimeException(e);
            }
        }
//        else {
//            // 토큰이 없는 경우 처리 차후 추가
//        }
//        return null;
        // 토큰이 없으면 그대로 요청을 전달 -> 게이트웨이에서 컷 401 로 처리
        return chain.filter(exchange);
    }
}
