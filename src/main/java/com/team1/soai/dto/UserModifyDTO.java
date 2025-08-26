package com.team1.soai.dto;

import lombok.Data;

@Data
public class UserModifyDTO {
    private String userId;
    private String userName;
    private String userRole;
    private String userPassword;
}