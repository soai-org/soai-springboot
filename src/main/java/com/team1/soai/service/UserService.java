package com.team1.soai.service;

import com.team1.soai.dto.UserDTO;
import com.team1.soai.dto.UserModifyDTO;
import com.team1.soai.mapper.UserManagementMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserManagementMapper userManagementMapper;

    public List<UserDTO> selectUserList(int offset, int limit, String order, boolean isASC) {

        RowBounds rowBounds = new RowBounds(offset, limit);
        return userManagementMapper.selectUserList(rowBounds, order, isASC);
    }

    // Select
    public UserDTO selectUserById(String id) {
        return userManagementMapper.selectUserById(id);
    }

    // Insert
    public void insertUser(UserDTO user) {
        userManagementMapper.insertUser(user);
    }

    // Modify
    public void modifyUser(UserModifyDTO user) {

        userManagementMapper.modifyUser(user);

    }

    // delete
    public void deleteUser(String userId) { userManagementMapper.deleteUser(userId); }
}
