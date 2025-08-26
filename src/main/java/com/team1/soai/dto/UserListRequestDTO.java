package com.team1.soai.dto;

import lombok.Data;

@Data
public class UserListRequestDTO {
    private int offset = 0;
    private int limit = 10;
    private String order = "userId";
    private boolean isASC = true;      // true: ASC, false: DESC
}