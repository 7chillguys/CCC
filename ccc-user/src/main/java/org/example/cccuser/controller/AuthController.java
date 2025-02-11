package org.example.cccuser.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.cccuser.dto.LoginReqDto;
import org.example.cccuser.service.AuthService;
import org.example.cccuser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    // DI : AuthService, TokenService(TokenPorvider와 비슷)
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginReqDto loginReqDto,
                                        HttpServletResponse response) {
        return ResponseEntity.ok( authService.login(loginReqDto, response) );
    }

    // 로그아웃 -> 로그인 이후 진행 -> 인증값 (토큰, 게이트웨이에서 설정한 값) 체크 : 헤더를 타고 전달
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("X-Auth-User") String email,
                                         @RequestHeader("Authorization") String accessToken) {
        authService.logout(email, accessToken);
        return ResponseEntity.ok("로그아웃 성공");
    }

}
