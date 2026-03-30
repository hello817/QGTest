package com.dorm.controller;

import com.dorm.dto.*;
import com.dorm.entity.RepairOrder;
import com.dorm.entity.User;
import com.dorm.mapper.UserMapper;
import com.dorm.service.UserService;
import com.dorm.utils.JwtUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dorm.utils.JwtUtils;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserMapper userMapper;
    @Autowired
    private UserService userService;
    //别忘了注入jwtutils
    @Autowired
    private JwtUtils jwtUtils;
    //登录接口
    @PostMapping("/public/login")
    public ResponseEntity<LoginResponse> loginAPI(@RequestBody LoginDto user){
        User loginUser = userService.login(user.getAccount(),user.getPassword());
        //这里要多写一个token生成，存到response对象里
        String token = jwtUtils.generateToken(loginUser.getId(),loginUser.getAccount(),loginUser.getRole());
        LoginResponse response = new LoginResponse();
        response.setId(loginUser.getId());
        response.setAccount(loginUser.getAccount());
        response.setRole(loginUser.getRole());
        response.setToken(token);
        return ResponseEntity.ok(response);
    }
    //注册接口
    @PostMapping("/public")
    public ResponseEntity<String> registerAPI(@RequestBody RegisterDto user){
        try {
            userService.register(user.getAccount(), user.getPassword(), user.getRole());
            return  ResponseEntity.status(201).body("注册成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
    //宿舍绑定接口
    @PutMapping("/private/{id}/dorm")
    public ResponseEntity<String> bindDorm(@PathVariable("id") long id, @RequestBody BindDormDto dormDto){
        userService.bindDorm(id,dormDto.getBuilding(),dormDto.getRoomNo());
        return ResponseEntity.status(200).body("绑定成功");
    }
    //修改密码
    @PutMapping("/private/{id}")
    public ResponseEntity<String> changePassword(@PathVariable("id") long id, @RequestBody ChangePasswordDto userDto){
        try{
            userService.changePassword(id,userDto.getOldPwd(),userDto.getNewPwd());
            return ResponseEntity.status(200).body("修改成功");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    //查找所有用户
    @GetMapping("/private")
    public List<User> selectAllUsers(){
        return userMapper.selectAllStudents();
    }
    @GetMapping("/private/{id}")
    public User selectById(@PathVariable("id") long id){
        return userMapper.selectById(id);
    }
    /*
    这是测试接口用的，不要在意（
    @GetMapping("/{id}")
    public User selectById(@PathVariable("id") long id){
        return userMapper.selectById(id);
    }
    */


}
