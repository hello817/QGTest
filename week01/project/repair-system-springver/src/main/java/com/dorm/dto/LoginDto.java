package com.dorm.dto;

import lombok.Data;

@Data//自动生成getter和setter
public class LoginDto {
      private String account;
      private String password;
}
