package com.team1.soai.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {

    private String userId;

    private String userPassword;

    private String userName;

    private String userRole;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
