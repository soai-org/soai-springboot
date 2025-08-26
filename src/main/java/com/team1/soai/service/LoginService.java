package com.team1.soai.service;

import com.team1.soai.JwtProvider;
import com.team1.soai.config.SecurityConfig;
import com.team1.soai.dto.LoginRequestDTO;
import com.team1.soai.dto.UserDTO;
import com.team1.soai.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtProvider jwtProvider;

    public String login(LoginRequestDTO loginDTO){



        String userId = loginDTO.getUserId();
        String rawUserPwd = loginDTO.getUserPassword();
        UserDTO userDTO = userMapper.getUserInfo(userId);

        //비밀번호 일치 여부 확인
        if(passwordEncoder.matches(rawUserPwd, userDTO.getUserPassword())){
            // JWT 토큰 반환
            String jwtToken = jwtProvider.generateJwtToken(userDTO.getUserId(), userDTO.getUserPassword());

            return "login Success + token : " + jwtToken;
        }

        return "login Falidfsdf..";




    }
}
