package com.dorm.service;

import com.dorm.entity.*;
import com.dorm.mapper.*;
import com.dorm.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import sun.security.util.Password;

import java.awt.*;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class UserService {
    //常量定义
    //正则表达式
//    private static final Pattern STUDENT_PATTERN = Pattern.compile("(3125/3225)\\d{6}$");//一定要有3125/3225前缀，后重接六位数字类型
//    private static final Pattern ADMIN_PATTERN = Pattern.compile("^0025\\d{6}$");//
    // 加密工具
    private static class EncryptUtil {
        public static String encrypt(String input) {
            try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(input.getBytes("UTF-8"));
                StringBuilder hex = new StringBuilder();
                for (byte b : hash) {
                    hex.append(String.format("%02x", b));
                }
                return hex.toString();
            } catch (Exception e) {
                return input;//加密失败就返回原输入
            }
        }
        public static boolean check(String input, String encrypted) {
            return encrypt(input).equals(encrypted);
        }
    }
    //登录
    private User login(String account ,String password) {
        try (SqlSession session = MyBatisUtil.getSqlSession()){
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.selectByAccount(account);
            /**
             * 检验账号
             * */
            if(user == null){
                //登录错误时抛出错误
                throw new RuntimeException("❌ 账号不存在");
            }
            /**
             * 检验密码
             */
            if(!EncryptUtil.check(password,user.getPassword())){
                throw new RuntimeException("❌ 密码错误");
            }
            return user;
        }
    }
    //注册
    private void register(String account,String password,String role) {
        try(SqlSession session = MyBatisUtil.getSqlSession()){
            UserMapper mapper = session.getMapper(UserMapper.class);
            if(mapper.selectByAccount(account)!=null){
                throw new RuntimeException("❌ 账号已存在");
            }
            User user = new User(account,EncryptUtil.encrypt(password),role);
            mapper.insert(user);
        }
    }
    //修改密码
    private void changePassword(User user,String oldPwd,String newPwd){
        try (SqlSession session = MyBatisUtil.getSqlSession()){
            UserMapper mapper = session.getMapper(UserMapper.class);
            
        }
    }
}
