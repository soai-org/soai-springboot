package com.team1.soai.mapper;

import com.team1.soai.dto.UserDTO;
import com.team1.soai.dto.UserModifyDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

@Mapper
public interface UserManagementMapper {
    List<UserDTO> selectUserList(
            RowBounds rowBounds,
            @Param("order") String order,
            @Param("isASC")boolean isASC
    );

    UserDTO selectUserById(String id);

    void insertUser(UserDTO user);

    void modifyUser(UserModifyDTO user);

    void deleteUser(String userId);
}
