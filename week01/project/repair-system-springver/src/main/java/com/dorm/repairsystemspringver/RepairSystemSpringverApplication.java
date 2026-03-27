package com.dorm.repairsystemspringver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.dorm.mapper")//将接口注入spring,首先spring要找到对象才能对对象进行管理
public class RepairSystemSpringverApplication {

    public static void main(String[] args) {
        SpringApplication.run(RepairSystemSpringverApplication.class, args);
    }

}
