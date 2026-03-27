package com.dorm.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String account;
    private String password;
    private String role;
}
