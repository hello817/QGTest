package com.dorm.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private long id;
    private String account;
    private String role;
    private String token;
}
