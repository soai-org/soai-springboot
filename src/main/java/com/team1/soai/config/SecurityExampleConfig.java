package com.team1.soai.config;

import com.team1.soai.JwtProvider;
import com.team1.soai.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityExampleConfig  extends WebSecurityConfigurerAdapter {

    private final UserMapper userMapper;
    public SecurityExampleConfig(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .anyRequest().permitAll();
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