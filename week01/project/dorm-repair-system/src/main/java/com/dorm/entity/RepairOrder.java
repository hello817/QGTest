package com.dorm.entity;
//和user表差不多逻辑
import org.apache.ibatis.javassist.bytecode.stackmap.BasicBlock;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.UUID;//生成唯一id用，这里因为util类语法很多所以只引用一个
public class RepairOrder {
    //先为报修状态和报修优先级添加枚举变量-status 和 priority
    public enum Status{
        PENDING("pending","待处理"),
        PROCESSING("processing","处理中"),
        COMPELITED("complited","已完成"),
        CANCELLED("cancelled","已取消");

        private final String code;
        private final String description;

        Status(String code,String description){
            this.code = code;
            this.description = description;
        }
        public String getCode(){return this.code;}
        public String getDescription() {return this.description;}
        // 静态方法：属于类本身，不属于任何实例
        public static Status fromCode(String code){
            for (Status s: values()){
                if(s.code.equals(code)){return s;}
            }
            throw new IllegalArgumentException("错误状态"+code);
        }
    }
    public enum Priority {
        HIGH("high","高"),
        MEDIUM("medium","中"),
        LOW("low","低");

        private final String code;
        private final String description;

        Priority(String code,String description){
            this.code = code;
            this.description = description;
        }
        public String getCode(){return this.code;}
        public String getDescription(){return this.description;}
        //formcode
        public static Priority formCode(String code){
            for (Priority s : values()){
                if(s.code.equals(code)){return s;}
            }
            throw new IllegalArgumentException("未知优先级"+code);
        }
    }
    //创建成员变量
    private long id;
    private String orderNo;//oderNumber
    private long studentId;//外键
    private String fixType;
    private String description;
    private String status;
    private String priority;
    private LocalDateTime creatTime;
    private LocalDateTime upodateTime;
    //关联对象
    private User student;

    public RepairOrder(){
        this.orderNo = generateOderNo();
        this.status = Status.PENDING.getCode();
        this.priority = Priority.MEDIUM.getCode();
        this.creatTime = LocalDateTime.now();
    }
    //实现generateOderNo（获取单号）
    //因为只有内部要用所以封装起来
    //单号生成规律：L+yyymmddHHmmss+随机数/L + 时间换算毫秒的x位 + 随机数......不行这样会循环单号
    private String generateOderNo(){
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        //获取四位随机数
        int random = (int)(Math.random()*10000);
        return 'L' + date + String.format("%04d",random);
    }
    //getter+setter

    public long getId() {return id;}
    public void setId(long id) {this.id = id;}

    public String getOrderNo(){return this.orderNo;}
    public void setOrderNo(String orderNo) {this.orderNo = orderNo;}

    public long getStudentId() {return this.studentId;}
    public void setStudentId(long studentId) {this.studentId = studentId;}

    public String getFixType(){return fixType;}
    public void setFixType(String fixType){this.fixType = fixType;}

    public String getDescription(){return description;}
    public void setDescription(String description){this.description = description;}

    public String getStatus() {return status;}
    public void setStatus(Status status){this.status = status.getCode();}
    public void setStatus(String status){this.status = status;}

    public String getPriority(){return priority;}
    public void setPriority(Priority priority){this.priority = priority.getCode();}
    public void setPriority(String priority){this.priority = priority;}

    public LocalDateTime getCreateTime(){return this.creatTime;}
    public void setCreateTime (LocalDateTime creatTime) {this.creatTime = creatTime;}

    public LocalDateTime getUpdateTime(){return this.upodateTime;}
    public void setUpdateTime(LocalDateTime upodateTime){this.upodateTime = upodateTime;}

    public User getStudent(){return student;}
    public void setStudent(User student){
        this.student = student;
        if (student != null){
            this.studentId = student.getId();
        }
    }

    //业务方法
    public String getStatusText(){
        try {
            return Status.fromCode(status).getDescription();
        }catch (Exception e){
            return "未知";
        }
    }
    public String getPriorityText(){
        try{
            return Priority.formCode(priority).getDescription();
        }catch (Exception e){
            return "中";//未知状态就返回默认值
        }
    }

    public boolean canCancel(){//判断能否取消(是否在pending状态)
        return Status.PENDING.getCode().equals(status);
    }
    //Tostring
    public String toString(){
        return String.format("报修单[单号=%s，设备=%s，状态=%s]",orderNo,fixType,status);
    }
}
