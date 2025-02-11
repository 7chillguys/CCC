package org.example.cccuser.dto;

import lombok.Data;
import lombok.ToString;
import org.antlr.v4.runtime.misc.NotNull;

/**
 * 원래는 ERD 구성후 진행
 * 회원가입시 전달된 데이터를 받는 그릇 용도 활용
 * json -> 객체에 바로 세팅되게 구성하는 용도
 */
@Data
@ToString
public class UserDto {
    // @NotNull : 향후 버전에서는 제거될수 있다. 널 허용 x 반드시 세팅하는 강제조항
    @NotNull
    private String email;
    @NotNull
    private String userName;
    @NotNull
    private String password;
    // 기본적인 롤 적용(기본값)
    private String role = "ROLE_USER"; // 등급, 레벨,등등 적용하여 구성가능함
    // ROLE_USER, ROLE_ADMIN, ROLE_SELLER
}
