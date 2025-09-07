package com.team1.soai.config;

import com.team1.soai.jwtTemple.JwtProvider;
import com.team1.soai.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    private final UserMapper userMapper;

    //JwtAuthenticationFilter를 SecurityConfig에서 필터 체인에 등록
    @Override protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);// <-- 세션 생성 금지

        // ===== 테스트용 설정 (JWT 인증 완전 비활성화) =====
        http.csrf().disable().authorizeRequests()
                .antMatchers("/user/login").permitAll()
                .antMatchers("/ws/**").permitAll()  // WebSocket 엔드포인트 허용
                .antMatchers("/chat-bot-ws/**").permitAll()
                .antMatchers("/chat-bot/**").permitAll()
                .antMatchers("/test-websocket.html").permitAll()  // 테스트 페이지 허용
                .antMatchers("/test-fastapi-websocket.html").permitAll()  // FastAPI WebSocket 테스트 페이지 허용
                .antMatchers("/static/**").permitAll()  // 정적 리소스 허용
                .antMatchers("/favicon.ico").permitAll()  // favicon 허용
                .antMatchers("/error").permitAll()  // 에러 페이지 허용
                .antMatchers("/").permitAll()  // 루트 경로 허용
                .antMatchers("/api/websocket/**").permitAll()  // WebSocket API 허용
                .antMatchers("/api/fastapi/websocket/**").permitAll()  // FastAPI WebSocket API 허용 (테스트용)
                .anyRequest().permitAll()  // 모든 요청 허용 (테스트용)
                .and();

        // ===== 원본 설정 (테스트 완료 후 주석 해제) =====
        /*
        http.csrf().disable().authorizeRequests()
                .antMatchers("/user/login").permitAll()
                .antMatchers("/ws/**").permitAll()  // WebSocket 엔드포인트 허용
                .antMatchers("/test-websocket.html").permitAll()  // 테스트 페이지 허용
                .antMatchers("/test-fastapi-websocket.html").permitAll()  // FastAPI WebSocket 테스트 페이지 허용
                .antMatchers("/static/**").permitAll()  // 정적 리소스 허용
                .antMatchers("/favicon.ico").permitAll()  // favicon 허용
                .antMatchers("/error").permitAll()  // 에러 페이지 허용
                .antMatchers("/").permitAll()  // 루트 경로 허용
                .antMatchers("/api/websocket/**").permitAll()  // WebSocket API 허용
                .antMatchers("/api/fastapi/websocket/**").authenticated()  // FastAPI WebSocket API는 인증 필요
                .anyRequest().authenticated().and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        */

        http.cors(); // CORS커스텀
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtProvider jwtTokenProvider(){
        return new JwtProvider(userMapper);
    }

}