package com.dorm.dto;

import lombok.Data;

import java.lang.ref.SoftReference;

@Data
public class ChangePasswordDto {
    String oldPwd;
    String newPwd;
}
