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

import javax.servlet.http.HttpServletResponse;


@Slf4j
@RestController
@RequiredArgsConstructor
class LoginController {

    private final LoginService loginService;

   /*
   *  로그인 성공 시 토큰 발급
   * */
    @PostMapping("/user/login")
    public String login(@RequestBody LoginRequestDTO loginDto, HttpServletResponse resp) {
        UserDTO userDTO = new UserDTO();

        try{
           return loginService.login(loginDto, resp);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/user/access/test")
    public String test(@AuthenticationPrincipal UserDTO user){
        if(user != null){
            return user.getUserId() + "님의 api 접근.";
        }
        return  "error zz";
    }
}