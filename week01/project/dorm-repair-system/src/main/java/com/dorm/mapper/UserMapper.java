package com.dorm.mapper;
//mapper即为DAO层

import com.dorm.entity.User;
import java.util.List;
import java.util.Map;
//mapper(DAO)层不要用class，要用interface定义接口类
public interface UserMapper {
    int insert(User user);
    User selectById(long id);
    User selectByAccount(String account);
    int update(User user);
    List<User> selectAllStudents();
    List<Map<String, Object>> countByRole();
}
