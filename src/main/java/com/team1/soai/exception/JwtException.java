package com.team1.soai.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;


public class JwtException extends RuntimeException{

    public JwtException(){}

    public JwtException(String message){
        super(message);
    }

}
