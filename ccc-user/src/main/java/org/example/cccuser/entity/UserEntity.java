package org.example.cccuser.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class UserEntity implements UserDetails {

    // emp_id -> primary key
    @Id
    private String emp_id;

    @Column(name = "name")
    private String name;

    private String password;

    private String department;

    private String position;

    private String email;

    private boolean enabled; // 이메일 인증 여부

    @Builder
    public UserEntity(String emp_id, String name, String password, String position, String email, boolean enabled) {
        this.emp_id = emp_id;
        this.name = name;
        this.password = password;
        this.position = position;
        this.email = email;
        this.enabled = enabled;
    }

    // ----------------
    // UserDetails 파트
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }



    @Override
    public String getUsername() {
        return emp_id;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
