package com.dorm.mapper;
//同user

import com.dorm.entity.RepairOrder;

import java.util.List;
import java.util.Map;

public interface RepairOrderMapper {
    int insert(RepairOrder order);//插入
    RepairOrder selectById(long id);//按id查找
    RepairOrder selectByOrderNo(String orderNo);//按单号查找
    int update(RepairOrder order);
    List<RepairOrder> selectByStudentId(long studentId);
    List<RepairOrder> selectAllWithStudent();
    List<RepairOrder> selectByStatus(String status);
    int updateStatus(long id, String status);
    int deleteById(long id);
    List<Map<String, Object>> countByStatus();
}
