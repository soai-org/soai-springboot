package com.team1.soai.service;

import com.team1.soai.jwtTemple.JwtProvider;
import com.team1.soai.dto.LoginRequestDTO;
import com.team1.soai.dto.UserDTO;
import com.team1.soai.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtProvider jwtProvider;

    public String login(LoginRequestDTO loginDTO, HttpServletResponse response) throws Exception{

        String userId = loginDTO.getUserId();
        String rawUserPwd = loginDTO.getUserPassword();
        UserDTO userDTO = userMapper.getUserInfo(userId);

        //비밀번호 일치 여부 확인
        if(passwordEncoder.matches(rawUserPwd, userDTO.getUserPassword())){
            // JWT 토큰 반환
            return jwtProvider.generateJwtToken(userDTO.getUserId());
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return String.valueOf(response.getStatus());
    }
}
