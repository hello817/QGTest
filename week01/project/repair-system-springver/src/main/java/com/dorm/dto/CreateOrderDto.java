package com.dorm.dto;

import lombok.Data;
import com.dorm.entity.RepairOrder;

@Data
public class CreateOrderDto {
    String fixType;
    String description;
    RepairOrder.Priority priority;
}
