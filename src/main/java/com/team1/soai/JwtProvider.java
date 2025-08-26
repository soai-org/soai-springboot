package com.team1.soai;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
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

    static Long EXPIRE_TIME = 60L * 60L * 1000L; // 1시간

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
    public String generateJwtToken(String userId, String userPassword){
        Date tokenExpiration = new Date(System.currentTimeMillis() + (EXPIRE_TIME));

        String jwtToken = JWT.create()
                .withSubject(userId) // token이름
                .withExpiresAt(tokenExpiration)
                .withClaim("userPassword", userPassword)
                .sign(this.getSign());

        return jwtToken;
    }

    /**
     * 토큰 검증
     *  - 토큰에서 가져온 email 정보와 DB의 유저 정보 일치하는지 확인
     *  - 토큰 만료 시간이 지났는지 확인
     * @param jwtToken
     * @return 유저 객체 반환
     */
    public UserDTO validToken(String jwtToken){
        try {
            String userId = JWT.require(this.getSign()).build().verify(jwtToken).getClaim("userId").asString();

            if(userId == null){
                return null;
            }

            //시간 만료 확인
            Date expiresAt = JWT.require(this.getSign()).acceptExpiresAt(EXPIRE_TIME).build().verify(jwtToken).getExpiresAt();

            if (!this.validExpiredTime(expiresAt)) {
                // 만료시간이 지났다.
                return null;
            }

            UserDTO tokenUser = userMapper.getUserInfo(userId);
            return tokenUser;

        }catch (Exception e){
            e.printStackTrace();
            return null;

        }
    }

    // 만료 시간 검증
    private boolean validExpiredTime(Date expiresAt){
        // LocalDateTime으로 만료시간 변경
        LocalDateTime localTimeExpired = expiresAt.toInstant().atZone(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        return LocalDateTime.now().isBefore(localTimeExpired);

    }

}
