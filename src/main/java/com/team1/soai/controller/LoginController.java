package com.team1.soai.controller;
import com.team1.soai.dto.LoginRequestDTO;
import com.team1.soai.dto.UserDTO;
import com.team1.soai.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
class LoginController {

    private final LoginService loginService;

   /*
   *  로그인 성공 시 토큰 발급
   * */
    @PostMapping("/user/login")
    public String login(@RequestBody LoginRequestDTO loginDto) {
        UserDTO userDTO = new UserDTO();
        String result = loginService.login(loginDto);
        return result;
    }
}