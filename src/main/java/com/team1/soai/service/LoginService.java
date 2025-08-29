package com.team1.soai.service;

import com.team1.soai.jwtTemple.JwtProvider;
import com.team1.soai.dto.LoginRequestDTO;
import com.team1.soai.dto.UserDTO;
import com.team1.soai.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtProvider jwtProvider;

    // 로그인 성공시 토큰을 발급한다.
    public ResponseEntity login(LoginRequestDTO loginDTO) throws IllegalArgumentException{

        String userId = loginDTO.getUserId();
        String rawUserPwd = loginDTO.getUserPassword();
        UserDTO userDTO = userMapper.getUserInfo(userId);

        if(userDTO == null) {
            // 아이디 없음
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("존재하지 않는 아이디입니다.");
        }
        if(!passwordEncoder.matches(rawUserPwd, userDTO.getUserPassword())) {
            // 비밀번호 불일치
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("비밀번호가 올바르지 않습니다.");
        }
        // 로그인 성공
        String token = jwtProvider.generateJwtToken(userDTO.getUserId());
        return ResponseEntity.ok(token);
    }

    //토큰 발급성공시 세션에 유저 정보를 담기 위해 유저 정보 select
    public UserDTO getUserInfo(LoginRequestDTO loginDTO){
        return userMapper.getUserInfo(loginDTO.getUserId());
    }
}
