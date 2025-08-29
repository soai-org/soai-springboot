package com.team1.soai.controller;
import com.team1.soai.dto.LoginRequestDTO;
import com.team1.soai.dto.UserDTO;
import com.team1.soai.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;


@Slf4j
@RestController
@RequiredArgsConstructor
class LoginController {

    private final LoginService loginService;

    /*
     *  로그인 성공 시 토큰 발급
     * */
    @PostMapping("/user/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginDto, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession();
            UserDTO userDTO = new UserDTO();

            ResponseEntity token = loginService.login(loginDto);

            if(token != null){
                userDTO = loginService.getUserInfo(loginDto);
                userDTO.setUserPassword("");
                session.setAttribute("userInfo", userDTO);
            }
            return token;

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/user/access/test")
    public String test(@AuthenticationPrincipal UserDTO user){
        return user.getUserId() + "님의 api 접근.";
    }
}