package com.team1.soai.dto;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;


@Data
public class UserDTO {

    private String userId;
    private String userPassword;
    private String userRole;

    // DB에서 가져온 userRole을 → Spring Security가 이해하는 GrantedAuthority로 변환
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return  Collections.singletonList(new SimpleGrantedAuthority(userRole));
    }
}
