package com.team1.soai.jwtTemple;

import com.team1.soai.dto.UserDTO;
import com.team1.soai.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 1. Request Header에서 JWT 꺼내기
 2. JwtProvider로 토큰 검증
 3. UserDetailsService or Mapper로 유저 정보 로드
 4. SecurityContextHolder에 인증 객체 세팅
 */

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        UserDTO userDTO = new UserDTO();

        //Authorization 헤더에서 JWT추출
        String header = request.getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")){
            String token = header.substring(7);

            //토큰 유효성 검증 ex)만료시간
            if(jwtProvider.validToken(token)){
                String userId = jwtProvider.getUserId(token);

                //DB 에서 유저정보 가져오기
                UserDTO userInfo = userMapper.getUserInfo(userId);
                System.out.println("JWT FIlter userInfo = " + userInfo);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userInfo, null, userInfo.getAuthorities()
                        );
                //시큐리티 컨텍스트 등록
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

}
