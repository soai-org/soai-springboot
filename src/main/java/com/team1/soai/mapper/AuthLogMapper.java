package com.team1.soai.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface AuthLogMapper {
    void insertAuthLog( String userId, LocalDateTime authDate, int loginResult);
}
