package org.example.cccuser.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cccuser.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name="members")
@Data
@NoArgsConstructor
public class UserEntity implements UserDetails {
    // email -> primary key
    // username, password, hp, address, role
    // enable (인증여부)
    @Id
    private String email;

    @Column(name="username")
    private String userName;

    private String password;
    private String hp;
    private String address;
    private String roles;
    private boolean enable; // 이메일 인증 여부

    @Builder
    public UserEntity(String email, String userName, String password, String hp, String address, String roles, boolean enable) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.hp = hp;
        this.address = address;
        this.roles = roles;
        this.enable = enable;
    }

    // ----------------
    // UserDetails 파트 -> 6개 메소드
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 역활 설정 => ROLE_USER
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        // email, password 통해서 로그인 예정
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;//UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;//UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;//UserDetails.super.isCredentialsNonExpired();
    }

    // 이메일 인증 여부를 체크
    @Override
    public boolean isEnabled() {
        return enable;//UserDetails.super.isEnabled();
    }
}
