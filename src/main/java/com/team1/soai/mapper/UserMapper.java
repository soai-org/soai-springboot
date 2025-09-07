package com.team1.soai.mapper;

import com.team1.soai.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    UserDTO getUserInfo(String userId);
}
