package com.team1.soai.controller;

import com.team1.soai.dto.*;
import com.team1.soai.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserManagementController {

    private final UserManagementService userManagementService;

    // 사용자 목록 조회 (JSON)
    @GetMapping("/list")
    public List<UserSelectListDTO> userList(@ModelAttribute UserListRequestDTO requestDTO) {
        return userManagementService.getUsersForManagement(
                requestDTO.getOffset(),
                requestDTO.getLimit(),
                requestDTO.getOrder(),
                requestDTO.isASC()
        );
    }

    // 사용자 등록
    @PostMapping("/add")
    public String addUser(@RequestBody UserDTO user) {
        try {
            userManagementService.insertUser(user);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to add user: " + e.getMessage());
        }
    }

    // 사용자 수정
    @PutMapping("/edit")
    public String editUser(@RequestBody UserModifyDTO user) {
        userManagementService.modifyUser(user);
        return "success";
    }

    // 사용자 삭제
    @DeleteMapping("/delete/{userId}")
    public String deleteUser(@PathVariable String userId) {
        userManagementService.deleteUser(userId);
        return "success";
    }
}
