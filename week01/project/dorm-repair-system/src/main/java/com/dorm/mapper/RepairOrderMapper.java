package com.dorm.mapper;
//同user
import com.dorm.entity.RepairOrder;
import java.util.List;
import java.util.Map;

public interface RepairOrderMapper {
    int insert(RepairOrder order);
    RepairOrder selectById(long id);
    RepairOrder selectByOrderNo(String orderNo);
    int update(RepairOrder order);
    List<RepairOrder> selectByStudentId(long studentId);
    List<RepairOrder> selectAllWithStudent();
    List<RepairOrder> selectByStatus(String status);
    int updateStatus(long id, String status);
    int deleteById(long id);
    List<Map<String, Object>> countByStatus();
}
