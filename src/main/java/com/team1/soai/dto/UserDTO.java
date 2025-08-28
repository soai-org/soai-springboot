package com.team1.soai.dto;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.time.LocalDateTime;

@Data
public class UserDTO {

    private String userId;

    private String userPassword;

    private String userName;

    private String userRole;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // DB에서 가져온 userRole을 → Spring Security가 이해하는 GrantedAuthority로 변환
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return  Collections.singletonList(new SimpleGrantedAuthority(userRole));
    }
}
