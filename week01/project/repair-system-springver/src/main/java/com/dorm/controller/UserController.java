package com.dorm.controller;

import com.dorm.dto.LoginDto;
import com.dorm.dto.RegisterDto;
import com.dorm.entity.User;
import com.dorm.mapper.UserMapper;
import com.dorm.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserMapper userMapper;
    //登录接口
    @PostMapping("/login")
    public User loginAPI(@RequestBody LoginDto user){
        UserService userService = new UserService();
        User loginUser = userService.login(user.getAccount(),user.getPassword());
        return loginUser;
    }
    //注册接口
    @PostMapping("/register")
    public String registerAPI(@RequestBody RegisterDto user){
        UserService userService = new UserService();
        userService.register(user.getAccount(),user.getPassword(),user.getRole());
        return "success";
    }
    //宿舍绑定接口

}
