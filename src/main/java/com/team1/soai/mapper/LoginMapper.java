package com.team1.soai.mapper;

import com.team1.soai.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LoginMapper {
    UserDTO userLogin(UserDTO ud);
}
