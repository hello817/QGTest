package com.dorm.controller;

import com.dorm.dto.BindDormDto;
import com.dorm.dto.ChangePasswordDto;
import com.dorm.dto.LoginDto;
import com.dorm.dto.RegisterDto;
import com.dorm.entity.RepairOrder;
import com.dorm.entity.User;
import com.dorm.mapper.UserMapper;
import com.dorm.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserMapper userMapper;
    @Autowired
    private UserService userService;
    //登录接口
    @PostMapping
    public ResponseEntity<User> loginAPI(@RequestBody LoginDto user){
        User loginUser = userService.login(user.getAccount(),user.getPassword());
        return ResponseEntity.ok(loginUser);
    }
    //注册接口
    @PostMapping
    public ResponseEntity<String> registerAPI(@RequestBody RegisterDto user){
        try {
            userService.register(user.getAccount(), user.getPassword(), user.getRole());
            return  ResponseEntity.status(201).body("注册成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    //宿舍绑定接口
    @PutMapping("/{id}/dorm")
    public ResponseEntity<String> bindDorm(@PathVariable("id") long id, @RequestBody BindDormDto dormDto){
        userService.bindDorm(id,dormDto.getBuilding(),dormDto.getRoomNo());
        return ResponseEntity.status(200).body("绑定成功");
    }
    //修改密码
    @PutMapping("/{id}")
    public ResponseEntity<String> changePassword(@PathVariable("id") long id, @RequestBody ChangePasswordDto userDto){
        try{
            userService.changePassword(id,userDto.getOldPwd(),userDto.getNewPwd());
            return ResponseEntity.status(200).body("修改成功");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    /*
    这是测试接口用的，不要在意（
    @GetMapping("/{id}")
    public User selectById(@PathVariable("id") long id){
        return userMapper.selectById(id);
    }
    */


}
