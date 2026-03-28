package com.dorm.controller;

import com.dorm.dto.CreateOrderDto;
import com.dorm.dto.UpdateOrderStatusDto;
import com.dorm.entity.RepairOrder;
import com.dorm.entity.User;
import com.dorm.mapper.RepairOrderMapper;
import com.dorm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class RepairOrderController {
    @Autowired
    RepairOrderMapper order;
    @Autowired
    UserService userService;

    //创建报修单
    @PostMapping("/{id}")
    public ResponseEntity<String> createOrder(@PathVariable("id") long studentId,@RequestBody CreateOrderDto orderDto){
        userService.createRepairOrder(studentId,orderDto.getFixType(),orderDto.getDescription(),orderDto.getPriority());
        return ResponseEntity.status(201).body("创建成功");
    }
    //取消报修单
    @DeleteMapping("{orderNo}")
    public ResponseEntity<String> cancelOrder(@PathVariable("orderNo") String orderNo){
        userService.cancelOrder(orderNo);
        return ResponseEntity.status(200).body("取消成功");
    }
    //更新报修单（管理员操作）
    @PutMapping("/{orderNo}")
    public ResponseEntity<String> updateOrderStatus(@PathVariable("orderNo") String orderNo, @RequestBody UpdateOrderStatusDto orderDto){
        userService.updateOrderStatus(orderNo,orderDto.getStatus());
        return ResponseEntity.status(200).body("更新成功");
    }
    //删除报修单
    @DeleteMapping("{orderNo}")
    public ResponseEntity<String> deleteOrder(@PathVariable("orderNo") String orderNo){
        try{
            userService.deleteOrder(orderNo);
            return ResponseEntity.status(200).body("删除成功");
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
