package com.dorm.entity;
//这里是用户实体类
//要获取系统时间需要引入一些头文件（雾）其实是Java的包
import java.time.*;
//因为觉得应该不会语法冲突所以直接引入全部了（）要用到的语句是LocalDateTime（获取时间）和DatetimeFormatter(时间标准格式化)
/**
 * 【实体类】
 * 类似C++的struct/class，但只有属性没有业务方法
 * Java中所有类都继承自Object（类似C++所有类都继承自Object）
 */
public class User {
    //用private隐藏内部实现
    private long id;//long即为c++的long long
    private String account;
    private String password;
    private String role;
    private String building;
    private String roomNumber;
    //接下来是时间类
    private LocalDateTime creatTime;
    private LocalDateTime updateTime;
    public static final String STUDENT = "student";
    //final类似const或者宏定义？一旦定义就不能更改
    public static final String ADMIN = "admin";

    //以下开始构造方法
    public User(){//没有传参时
        this(null,null,null,null,null);//这里的实现实际上就是this调用了有五个传参时的构造函数User（）
    }

    public User(String account , String password , String role){//注册时使用,无需填写宿舍号
        this(account,password,role,null,null);
    }

    public User(String account,String password,String role,String building ,String roomNumber){
        //赋值环节
        this.account = account;
        this.password = password;
        this.role = role;
        this.building = building;
        this.roomNumber = roomNumber;
        this.creatTime = LocalDateTime.now();//自动设置为当前时间
    }

    //接下来是会用到的函数
    //基础数据访问:
    //id
    public long getId(){return id;}
    public void setId(long id){this.id = id;}
    //account
    public String getAccount(){return this.account;}
    public void setAccount(String account){
        if (account!=null && account.length() == 10){
            this.account = account;
        }else{
            throw new IllegalArgumentException("账号必须为3125为首的十位数字(学号)");
            //异常输出语句
        }
    }
    //password
    public String getPassword(){return this.password;}
    public void setPassword(String password){this.password = password;}
    //role
    public String getRole(){return this.role;}
    public void setRole(String role){this.role = role;}
    //building
    public String getBuilding(){return this.building;}
    public void setBuilding(String building){this.building = building;}
    //roomNumber
    public String getRoomNumber(){return this.roomNumber;}
    public void setRoomNumber(String roomNumber){this.roomNumber = roomNumber;}
    //createTime因为不会有命名冲突所以不用加this限制，updateTime同理
    public LocalDateTime getCreatTime(){return creatTime;}
    public void setCreatTime(LocalDateTime creatTime){this.creatTime = creatTime;}
    //updateTime
    public LocalDateTime getUpdateTime() {return updateTime;}
    public void setUpdateTime(LocalDateTime updateTime) {this.updateTime = updateTime;}

    //以下为业务方法

    //判断身份
    public boolean isStudent() {return STUDENT.equals(this.role);}
}
