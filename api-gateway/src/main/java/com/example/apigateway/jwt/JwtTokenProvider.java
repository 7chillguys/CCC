package com.example.apigateway.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT토큰 생성, 검증, 필요한 정보 추출등 관리
 */
@Component
public class JwtTokenProvider {
    // 맴버 변수 형태 구성
    // 토큰 생성시 필요한 비밀키 -> 재료값 (외부 노출 x) -> @Value("...")
    @Value("${jwt.token.raw_secret_key}")
    private String rawSecretKey;

    // 키에 대한 만료시간 (필요시 사전에 정의해서 진행 가능) -> 변수로 사용
    @Value("${jwt.expiration}")
    private long expiration;

    // 엑세스토큰은 통상 1시간, 리플레시토큰 통상 7일 정도 부여 (컨셉)
    // 비밀키(rawSecretKey) 바이트배열로 변경 -> 가공처리(HMAC알고리즘적용)후 사용 -> 변수 정의
    private SecretKey secretKey;

    // 메소드
    // 빈 초기화 된후 자동 호출 -> 초기기점(1회성) -> 초기화 작업!!
    @PostConstruct
    public void init() {
        System.out.println("게이트웨이 시크릿 초기화 처리");
        // 최초 1회 생성시 자동 호출되는 메소드
        // 시크릿키를 사용에 맞게 변환 처리
        this.secretKey = Keys.hmacShaKeyFor(rawSecretKey.getBytes());
    }
    // 기능
    // 토큰 생성 -> 기본 재료는 이메일
    public String createToken(String email){
        // 추가 정보를 담는 그릇 -> Claims
        Claims claims = Jwts.claims().setSubject(email); // 이메일 밖에 없어서 재사용함
        // map 계열, 중복제거!!, 키는 고유한게 등록
        claims.put("email", email);
        // 추가 정보 있으면 추가 ... 없으면 생략

        // 시간 세팅
        Date now = new Date(); // 현재시간
        Date expiryDate = new Date(now.getTime() + expiration); // 현재시간+만료시간(양)

        return Jwts.builder()
                // 추가정보를 토큰에 삽입
                .setClaims( claims )
                // 시간 -> 토큰생성시간, 만료시간 세팅되야함
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                // 서명 (토큰에는 서명이 기입 -> 이를 통해서 향후 검증 활용)
                .signWith(this.secretKey, SignatureAlgorithm.HS256) // 기본 알고리즘 적용
                .compact();
    }
    // 토큰 유효성 검사
    // 사용자 페이지 요청 -> http 헤더 정보에 jwt 토큰(엑세스 토큰)이 전달 -> 검증
    public boolean validateToken(String token){
        // 토큰 자체에 하자 체크
        try{
            // 암호를 풀고 추가 정보를 획득? 하는 행위에 문제가 없는지 체크
            Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    // 주어진 토큰을 통해서 이메일 획득
    public String getEmailFromToken(String token){
        // 토큰 -> 추가 정보를 가진 그릇 획득 -> 이메일(향후 추가 정보 가능) 획득
        try{
            // 추가 정보를 가진 그릇 획득
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(this.secretKey).build()
                    .parseClaimsJws(token)
                    .getBody();
            // 이메일 키값을 넣어서 획득
            return claims.get("email", String.class);
        }catch (ExpiredJwtException e){
            System.out.println("getEmailFromToken() 기간 만료 토큰 오류");
            throw e;
        }catch (Exception e){
            System.out.println("getEmailFromToken() 토큰 디코딩과정 일반 오류(서명오류), 토큰 조작(손실)되었다 ");
            throw e;
        }
    }
}
