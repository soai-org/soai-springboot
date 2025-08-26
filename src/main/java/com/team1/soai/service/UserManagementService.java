package com.team1.soai.service;

import com.team1.soai.dto.UserDTO;
import com.team1.soai.dto.UserModifyDTO;
import com.team1.soai.dto.UserSelectListDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementService {
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    public List<UserSelectListDTO> getUsersForManagement(int offset, int limit, String order, Boolean isASC) {
        List<UserDTO> userDTOList = userService.selectUserList(offset, limit, order, isASC);

        String finalOrder = (order == null || order.isEmpty()) ? "userId" : order;
        boolean finalIsASC = (isASC == null) ? false : isASC;

        return userDTOList.stream()
                .map(user -> new UserSelectListDTO(user.getUserId(), user.getUserName(), user.getUserRole()))
                .collect(Collectors.toList());
    }

    // Insert
    public void insertUser(UserDTO user) {
        if (user.getUserPassword() != null && !user.getUserPassword().isEmpty()) {
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        }
        userService.insertUser(user);
    }

    // Modify
    public void modifyUser(UserModifyDTO user) {
        UserDTO existing = userService.selectUserById(user.getUserId());

        if(user.getUserPassword() != null && !user.getUserPassword().isEmpty()) {
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword()));
        } else {
            user.setUserPassword(existing.getUserPassword());
        }
        if(user.getUserName() == null || user.getUserName().equals("")) {
            user.setUserName(userService.selectUserById(user.getUserId()).getUserName());
        }
        userService.modifyUser(user);
    }

    // delete
    public void deleteUser(String userId) { userService.deleteUser(userId); }
}
