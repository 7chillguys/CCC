package org.example.cccuser.service;

import org.example.cccuser.dto.UserDto;
import org.example.cccuser.entity.UserEntity;
import org.example.cccuser.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 회원관련 비즈니스 로직 처리
 */
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private JavaMailSender mailSender; // 자바 메일 전송 라이브러리

    public void createUser(UserDto userDto) {
        // 1. 입력값 검증
        // 값 -> 검증가능!! -> 오류 -> 예외 던지기!! -> 생략
        // UI단을 사용 -> vaildation 사용, restapi -> 값에서 체크
        if( userDto.getEmail() == null || userDto.getEmail().isEmpty() ) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        // 기존 가입자인지? -> 이메일 중복 체크!! -> 레포지토리 커스텀 구성
        // findBy + 컬럼명() => findByEmail()
        if( userRepository.findByEmail(userDto.getEmail()).isPresent() ) {
            throw new IllegalArgumentException("Email already exists");
        }
        if( userDto.getUserName() == null || userDto.getUserName().isEmpty() ) {
            throw new IllegalArgumentException("userName cannot be empty");
        }
        if( userDto.getPassword() == null || userDto.getPassword().isEmpty() ) {
            throw new IllegalArgumentException("password cannot be empty");
        }

        // 2. 엔티티 생성
        UserEntity userEntity = UserEntity.builder()
                .email(userDto.getEmail())
                .userName(userDto.getUserName())
                .password( passwordEncoder.encode(userDto.getPassword()) )
                .roles("ROLE_USER")
                .enable(false)
                .build();

        // 3. 엔티티 저장 -> 디비에 members 테이블에 저장
        userRepository.save(userEntity);

        // 4. 인증 이메일 발송
        sendVaildEmail( userEntity );
    }
    // 이메일 전송 메소드
    private void sendVaildEmail(UserEntity userEntity) {
        // 이메일 내용 안에 인증 요청을 GET방식으로 요청하도록 URL을 구성
        // 게이트웨이에 프리패스로 URL 등록되어야 한다
        // URL 합당하게 처리되기 위해서 토큰(일종의)값 같이 전달
        // 일종의 토큰(고유값)을 발급 -> 레디스에 저장(이메일 기준), 유저하게 발송
        // 유저는 이메이르 확인 -> 클릭 -> 게이트웨이->서비스진입 -> 전달된 토큰과 레디스에 저장된 토큰 일치 -> 인증
        // 필요시 레디스 저장할때 만료시간 지정 or 토큰 자체에 만료시간 적용 할수 있다

        // 1. 토큰 발행 -> 단순하게 기기 고유값 생성
        String token = UUID.randomUUID().toString();
        // 2. redis 저장 -> 키는 토큰(외부공개->가입자에게만 전달), 값은 이메일(토큰->이메일추출->db에 존재하는가?), 만료시간 6시간,
        //    이메일 인증절차 -> 유효한 이메일인지를 검증하는 단계
        redisTemplate.opsForValue().set(token,
                userEntity.getEmail(), 6, TimeUnit.HOURS);
        // 3. URL 구성 -> 가입한 사용자의 이메일에서 인증메일에 전송된 링크
        String url = "http://localhost:8080/user/vaild?token=" + token;
        // 4. 메일 전송 (받는 사람주소, 제목, 내용)
        sendMail( userEntity.getEmail(), "Email 인증", "링크를 눌러서 인증: " + url );
    }
    private void sendMail(String email, String subject, String content) {
        // 1. 메세지 구성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(content);
        // 2. 전송
        mailSender.send(message);
    }
    // enable 컬럼 : f->t (유효할때만)
    public void updateActivate(String token) {
        // 1. 레디스 토큰 -> 이메일 획득
        String email = (String) redisTemplate.opsForValue().get(token);
        // 2. 없다면 -> 잘못된 토큰 혹은 만료된 토큰 -> 예외 처리(토큰오류)
        if(email == null) {
            throw new IllegalArgumentException("잘못된 토큰 혹은 만료된 토큰");
        }
        // 3. 존재한다면 -> 이메일(id, pk)  -> 엔티티 획득
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow( ()-> new IllegalArgumentException("사용자 오류(존재x)") );
        // 4. enable 컬럼 : f->t => 저장
        userEntity.setEnable(true);
        userRepository.save(userEntity);
        // 5. 레디스 토큰 삭제
        redisTemplate.delete(token);
    }
}
