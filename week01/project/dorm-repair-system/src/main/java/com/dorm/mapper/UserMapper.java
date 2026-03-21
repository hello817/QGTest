package com.dorm.mapper;
//mapper即为DAO层

import com.dorm.entity.User;//要映射的对象
import java.util.Map;//类似stl的map
import java.util.List;//类似vector，存储包装数据map，为了存储对应身份的人数
//mapper(DAO)层不要用class，要用interface定义接口类
public interface UserMapper {
    int insert(User user);//返回成功插入的用户表单数量，更好的判断插入有没有被执行
    User selectById(long id);
    User selectByAccount(String account);//两个都是返回查找到的学生
    int update(User user);
    List<User> selectAllStudents();
    List<Map<String,Object>> countByRole();//当前有多少人数（管理员加用户），object类型是用来自动判断插入的类
}
