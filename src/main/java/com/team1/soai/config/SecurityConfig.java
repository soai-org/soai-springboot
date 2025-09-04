package com.team1.soai.config;

import com.team1.soai.jwtTemple.JwtAuthenticationFilter;
import com.team1.soai.jwtTemple.JwtProvider;
import com.team1.soai.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig  extends WebSecurityConfigurerAdapter {

    private final UserMapper userMapper;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    //JwtAuthenticationFilter를 SecurityConfig에서 필터 체인에 등록
    @Override protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);// <-- 세션 생성 금지

        http.csrf().disable().authorizeRequests()
                .antMatchers("/user/login").permitAll()
                .antMatchers("/ws/**").permitAll()
                .antMatchers("/api/websocket/**").permitAll()
                .antMatchers("/test-nlp-websocket.html").permitAll()    // 사용하지 않는다면 삭제 필요
                .anyRequest().authenticated().and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

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