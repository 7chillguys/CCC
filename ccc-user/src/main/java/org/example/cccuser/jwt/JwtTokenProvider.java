package org.example.cccuser.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * JWT토큰 생성, 검증, 필요한 정보 추출등 관리
 */
@Component
public class JwtTokenProvider {
    // 맴버 변수 형태 구성
    // 토큰 생성시 필요한 비밀키 -> 재료값 (외부 노출 x) -> @Value("...")
    @Value("${jwt.token.raw_secret_key}")
    private String rawSecretKey;

    // 엑세스 토큰 만료시간
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    // 리플레시 토큰 만료시간
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // JWT 시크릿키 처리 -> 필요할때 호출해서 사용 -> 서명용도
    private Key getSecretKey() {
        return Keys.hmacShaKeyFor(rawSecretKey.getBytes());
    }

    // 엑세스토큰 생성(발급) : 재료 = 이메일, 롤, ...
    public String createAccessToken(String email, String role) {
        return createToken(email, role, accessTokenExpiration); // 1시간 만료시간
    }

    // 리플레시토큰 생성(발급) : 재료 x
    public String createRefreshToken(){
        return createToken(null, null, refreshTokenExpiration); // 7일 만료시간
    }

    // 토큰 생성 통합 : 이메일(or null), 롤(or null), 만료시간
    public String createToken(String email, String role, long expiration) {
        // JWT = 헤더(header) + 페이로드(payload) + 서명(signature)
        // 페이로드에서 추가 정보 기입 => Claims => 데이터는 키-값 세트
        Map<String, Object> claims = new HashMap<>();
        // 이메일, 롤 적용, 리플레시 토큰은 통과(처리 x)
        if(email != null) {
            claims.put("email", email); // 엑세스 토큰용
        }
        if(role != null) {
            claims.put("role", role);   // 엑세스 토큰용
        }
        // 시간단위 1/1000 초 => 단위는 밀리 세컨즈
        // 현재시간, 현재시간+만료시간(양) = 만료예정 미래시간
        Date now = new Date(); // 현재시간
        Date expiryDate = new Date(now.getTime() + expiration); // 현재시간+만료시간(양)

        return Jwts.builder()
                .setClaims( claims )    // 추가정보를 토큰에 삽입
                .setIssuedAt(now)       // 현재 시간 세팅(토큰 생성 시간)
                .setExpiration(expiryDate)  // 토큰 만료 시간
                .signWith(getSecretKey(), SignatureAlgorithm.HS256) // 기본 알고리즘 적용
                .compact();
    }

    // 토큰 검증
    public boolean validateToken(String token){
        // 토큰 자체에 하자 체크
        try{
            // 암호를 풀고 추가 정보를 획득? 하는 행위에 문제가 없는지 체크
            // 열쇠(암호키)를 넣고 문을 연다(토큰 디코딩(해석된다))
            Jwts.parserBuilder().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /*
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
    */
}
