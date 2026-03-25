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
    public static class EncryptUtil {
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
    public User login(String account ,String password) {
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
    public void register(String account,String password,String role) {
        try(SqlSession session = MyBatisUtil.getSqlSession()){
            UserMapper mapper = session.getMapper(UserMapper.class);
            if(mapper.selectByAccount(account)!=null){
                throw new RuntimeException("❌ 账号已存在");
            }
            User user = new User(account,EncryptUtil.encrypt(password),role);
            mapper.insert(user);
        }
    }
    //宿舍绑定
    public void bindDorm(User user,String building,String roomNo){
        try(SqlSession session = MyBatisUtil.getSqlSession()){
            UserMapper mapper = session.getMapper(UserMapper.class);
            user.setBuilding(building);
            user.setRoomNumber(roomNo);
            mapper.update(user);
        }
    }
    //创建报修单
    public String createRepairOrder(long studentId,String fixType,String description,RepairOrder.Priority priority) {
        try(SqlSession session = MyBatisUtil.getSqlSession()){
            //注意这里要用报修单的映射
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            RepairOrder order = new RepairOrder();
            order.setStudentId(studentId);
            order.setFixType(fixType);
            order.setDescription(description);
            order.setPriority(priority);
            mapper.insert(order);
            return order.getOrderNo();
        }
    }
    //删除报修单
    public void cancelOrder(RepairOrder order){
        try(SqlSession session = MyBatisUtil.getSqlSession()){
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            order.setStatus(RepairOrder.Status.CANCELLED);
            mapper.update(order);
        }
    }
    //修改密码
    public void changePassword(User currentUser,String oldPwd,String newPwd){
        try (SqlSession session = MyBatisUtil.getSqlSession()){
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.selectById(currentUser.getId());
            if (!EncryptUtil.check(oldPwd,user.getPassword())){
                throw new RuntimeException("❌ 原密码错误");
            }
            user.setPassword(EncryptUtil.encrypt(newPwd));
            mapper.update(user);
        }
    }
    /*
    ---------管理员业务
    需要迁移：
        1.更新报修单状态
        2.删除报修单
     */
    //更新报修单
    public void updateOrderStatus(RepairOrder order,RepairOrder.Status newStatus){
        try(SqlSession session = MyBatisUtil.getSqlSession()){
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            order.setStatus(newStatus);
            mapper.update(order);
        }
    }
    //删除报修单
    public void deleteOrder(String orderNo){
        try(SqlSession session = MyBatisUtil.getSqlSession()){
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            RepairOrder order = mapper.selectByOrderNo(orderNo);
            if(order == null){
                throw new RuntimeException("报修单不存在");
            }
            mapper.deleteById(order.getId());
        }catch(RuntimeException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
