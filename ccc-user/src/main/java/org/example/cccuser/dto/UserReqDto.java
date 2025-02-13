package org.example.cccuser.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원정보 조회시 요청 데이터를 받는 그릇 용도 활용
*/

@Data
@NoArgsConstructor
public class UserReqDto {
    private String empId;
    private String name;
    private String department;
    private String position;
    private String email;

    @Builder
    public UserReqDto(String empId, String name, String department, String position, String email) {
        this.empId = empId;
        this.name = name;
        this.department = department;
        this.position = position;
        this.email = email;
    }
}
