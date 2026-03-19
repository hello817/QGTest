package com.dorm.entity;
//和user表差不多逻辑
import java.time.*;
import java.util.UUID;//生成唯一id用，这里因为util类语法很多所以只引用一个
public class RepairOrder {
    //先为报修状态和报修优先级添加枚举变量-status 和 priority
    public enum Status{
        /**枚举常量，每个常量都带两个数据,有待处理，处理中，已完成，取消，
         * 而且枚举常量因为完全就是一个对象类型，比较时会很安全（对象无法和其他类型甚至是和自己不同的类型比较）
         */
        //这里其实是调用了后面的构造函数，前面作为code使用，后面作为description使用
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
        public static Status formCode(String code){
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
    
}
