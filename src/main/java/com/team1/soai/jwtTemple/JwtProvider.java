package com.team1.soai.jwtTemple;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.team1.soai.dto.UserDTO;
import com.team1.soai.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

/**
 * JWT 토큰 관련 메소드
 * */
@Configuration
@RequiredArgsConstructor
public class JwtProvider {

    private final UserMapper userMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    private Algorithm getSign(){
        return Algorithm.HMAC512(secretKey);
    }

    @PostConstruct
    protected void init(){
        this.secretKey = Base64.getEncoder().encodeToString(this.secretKey.getBytes());
    }



    //JWT 토큰 생성
    public String generateJwtToken(String userId){
        // 1시간
        Date tokenExpiration = new Date(System.currentTimeMillis() + 2000L * 60 * 60);
        String jwtToken = JWT.create()
                .withSubject(userId) // token이름
                .withExpiresAt(tokenExpiration)
                .withClaim("userId", userId)  // 검증할 claim
                .sign(this.getSign());

        return jwtToken;
    }

    /**
     * 토큰 검증
     *  - 토큰에서 가져온 userId 정보와 DB의 유저 정보 일치하는지 확인
     *  - 토큰 만료 시간이 지났는지 확인
     * @param jwtToken
     * @return boolean
     */
    public boolean validToken(String jwtToken){
        try {
            DecodedJWT decodedJWT = JWT.require(this.getSign()).build().verify(jwtToken);

            //token 에서 userId 추출 및 검증
            String userId = decodedJWT.getClaim("userId").asString();

            UserDTO userDto = userMapper.getUserInfo(userId);
            if(userDto == null){
                return false;
            }

            //시간 만료 확인
            Date expiresAt = decodedJWT.getExpiresAt();
            if (!this.validExpiredTime(expiresAt)) {
                return false;
            }
            return true;

        }catch (JWTVerificationException e){
            e.printStackTrace();
            return false;
        }
    }

    // 만료 시간 검증
    private boolean validExpiredTime(Date expiresAt){
        // LocalDateTime으로 만료시간 변경
        LocalDateTime localTimeExpired = expiresAt.toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        return LocalDateTime.now().isBefore(localTimeExpired);

    }

    // 토큰에서 getUserId 꺼내는 메서드
    public String getUserId(String token){
        try {
            return JWT.require(this.getSign())
                    .build()
                    .verify(token)
                    .getClaim("userId")
                    .asString();
        }catch (Exception e){
            return null;
        }
    }

}
