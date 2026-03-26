package com.dorm.service;

import com.dorm.entity.RepairOrder;
import com.dorm.entity.User;
import com.dorm.mapper.RepairOrderMapper;
import com.dorm.mapper.UserMapper;
//import com.dorm.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RepairOrderMapper repairOrderMapper;
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
            User user = userMapper.selectByAccount(account);
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
    //注册
    @Transactional
    public void register(String account,String password,String role) {
            if(userMapper.selectByAccount(account)!=null){
                throw new RuntimeException("❌ 账号已存在");
            }
            User user = new User(account, EncryptUtil.encrypt(password),role);
            userMapper.insert(user);
    }
    //宿舍绑定
    @Transactional
    public void bindDorm(User user,String building,String roomNo){
            user.setBuilding(building);
            user.setRoomNumber(roomNo);
            userMapper.update(user);
    }
    //创建报修单
    @Transactional
    public String createRepairOrder(long studentId,String fixType,String description,RepairOrder.Priority priority) {
            //注意这里要用报修单的映射
            RepairOrder order = new RepairOrder();
            order.setStudentId(studentId);
            order.setFixType(fixType);
            order.setDescription(description);
            order.setPriority(priority);
            repairOrderMapper.insert(order);
            return order.getOrderNo();
    }
    //删除报修单
    @Transactional
    public void cancelOrder(RepairOrder order){
            order.setStatus(RepairOrder.Status.CANCELLED);
            repairOrderMapper.update(order);
    }
    //修改密码
    @Transactional
    public void changePassword(User currentUser,String oldPwd,String newPwd){
            User user = userMapper.selectById(currentUser.getId());
            if (!EncryptUtil.check(oldPwd,user.getPassword())){
                throw new RuntimeException("❌ 原密码错误");
            }
            user.setPassword(EncryptUtil.encrypt(newPwd));
            userMapper.update(user);
    }
    /*
    ---------管理员业务
    需要迁移：
        1.更新报修单状态
        2.删除报修单
     */
    //更新报修单
    @Transactional
    public void updateOrderStatus(RepairOrder order,RepairOrder.Status newStatus){
            order.setStatus(newStatus);
            repairOrderMapper.update(order);
    }
    //删除报修单
    @Transactional
    public void deleteOrder(String orderNo) {
        try {
            RepairOrder order = repairOrderMapper.selectByOrderNo(orderNo);
            if (order == null) {
                throw new RuntimeException("报修单不存在");
            }
            repairOrderMapper.deleteById(order.getId());
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
