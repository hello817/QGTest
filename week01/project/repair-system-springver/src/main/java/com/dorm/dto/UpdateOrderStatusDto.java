package com.dorm.dto;

import lombok.Data;
import com.dorm.entity.RepairOrder;
@Data
public class UpdateOrderStatusDto {
    RepairOrder.Status status;
}
