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
public class UserDto{
    @NotNull
    private String empId;
    @NotNull
    private String name;
    @NotNull
    private String password;
    @NotNull
    private String department;
    @NotNull
    private String position;
    @NotNull
    private String email;

}