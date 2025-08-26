package com.team1.soai.service;

import com.team1.soai.dto.UserDTO;
import com.team1.soai.dto.UserModifyDTO;
import com.team1.soai.dto.UserSelectListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementService {
    private final UserService userService;

    public List<UserSelectListDTO> getUsersForManagement(int offset, int limit, String order, boolean isASC) {
        List<UserDTO> userDTOList = userService.selectUserList(offset, limit, order, isASC);

        return userDTOList.stream()
                .map(user -> new UserSelectListDTO(user.getUserId(), user.getUserName(), user.getUserRole()))
                .collect(Collectors.toList());
    }

    // Insert
    public void insertUser(UserDTO user) {
        userService.insertUser(user);
    }

    // Modify
    public void modifyUser(UserModifyDTO user) {
        if(user.getUserPassword() == null || user.getUserPassword().equals("")) {
            user.setUserPassword(userService.selectUserById(user.getUserId()).getUserPassword());
        }
        if(user.getUserName() == null || user.getUserName().equals("")) {
            user.setUserName(userService.selectUserById(user.getUserId()).getUserName());
        }
        userService.modifyUser(user);
    }

    // delete
    public void deleteUser(String userId) { userService.deleteUser(userId); }
}
