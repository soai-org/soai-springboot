package com.team1.soai.aspect;

import com.team1.soai.dto.LoginRequestDTO;
import com.team1.soai.mapper.AuthLogMapper;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthenticationAspect {

    private final AuthLogMapper authLogMapper;

    //pointcut - loginService의 login메서드에만 적용
    @Pointcut("execution(* com.team1.soai.service.LoginService.login(..))")
    public void loginMethod(){}

    @AfterReturning(pointcut = "loginMethod()", returning = "result")
    public void afterLoginSuccess(JoinPoint joinPoint, Object result){
        LoginRequestDTO dto = (LoginRequestDTO)joinPoint.getArgs()[0];
        int loginResult = 0;

        if(result instanceof ResponseEntity){
            ResponseEntity<?> response =(ResponseEntity<?>) result;

            //Http 상태코드로 성공 여부 판정
            if(true == response.getStatusCode().is2xxSuccessful()){
                loginResult = 1;
            }
        }
        authLogMapper.insertAuthLog(dto.getUserId(), LocalDateTime.now(), loginResult);
    }
}
