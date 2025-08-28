package com.team1.soai.jwtTemple;

import com.auth0.jwt.exceptions.JWTVerificationException;
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
import java.rmi.RemoteException;

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

        try {
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
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userInfo, null, userInfo.getAuthorities()
                            );
                    //시큐리티 컨텍스트 등록
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }else{
                    throw new RemoteException("토큰 인증 실패.");
                }
            }

            filterChain.doFilter(request, response);
        }catch (Exception e) {
            // 토큰 만료나 변조 시
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("토큰이 유효하지않습니다." + response.getStatus());
        }


    }

}
