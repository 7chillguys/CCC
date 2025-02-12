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
    @Column(name = "emp_id")
    private String empId;

    @Column(name = "name")
    private String name;

    private String password;

    private String department;

    private String position;

    private String email;

    private boolean enabled; // 이메일 인증 여부

    @Builder
    public UserEntity(String empId, String name, String password, String department, String position, String email, boolean enabled) {
        this.empId = this.empId;
        this.name = name;
        this.password = password;
        this.department = department;
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
        return empId;
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
