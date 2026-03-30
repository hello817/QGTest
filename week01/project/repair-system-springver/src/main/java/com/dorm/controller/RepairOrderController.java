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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class RepairOrderController {
    @Autowired
    RepairOrderMapper order;
    @Autowired
    UserService userService;

    //创建报修单
    @PostMapping("/{id}")
    public ResponseEntity<String> createOrder(@PathVariable("id") long studentId, @RequestPart("order") CreateOrderDto orderDto, @RequestPart(value = "image",required = false/*非必传*/) MultipartFile image){
        byte[] imageData = null;
        //这里会抛出io错误
        if(image != null&&!image.isEmpty()){
            //特判防bug
            if(image.getSize() > 10*1024*1024){
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("图片大小不得大于10MB");
            }
            try {
                imageData = image.getBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        userService.createRepairOrder(studentId,orderDto.getFixType(),orderDto.getDescription(),orderDto.getPriority(),imageData);
        return ResponseEntity.status(201).body("创建成功");
    }
    //取消报修单
    @PutMapping("{orderNo}/status")
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
    //查询所有报修单(与学生绑定的)
    @GetMapping
    public List<RepairOrder> selectAllOrder(){
        return order.selectAllWithStudent();
    }
    //学生id查询报修单
    @GetMapping("/user/{id}")
    public List<RepairOrder> selectByStudentId(@PathVariable("id") long id){
        return order.selectByStudentId(id);
    }
    //根据单号查询
    @GetMapping("/{orderNo}")
    public RepairOrder selectByOrderNo(@PathVariable("orderNo") String orderNo){
        return order.selectByOrderNo(orderNo);
    }
}
