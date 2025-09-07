package com.team1.soai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSelectListDTO {
    private String userId;

    private String userName;

    private String userRole;
}
