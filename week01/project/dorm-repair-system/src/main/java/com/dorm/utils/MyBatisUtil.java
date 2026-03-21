package com.dorm.utils;

import jdk.nashorn.internal.runtime.regexp.JoniRegExp;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;

//这里也可以直接import org.apache.ibatis.session.*全部子包，导入这些细节是为了学习用法
//这是一个辅助工具类，用来创建数据库sql会话的工厂
public class MyBatisUtil {
    private static SqlSessionFactory factory;//封装这个类的连接工厂,静态变量所有这个类的对象共享

    static{
        try {
            //连接函数
            InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");//获取xml中的配置文件
            factory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("MyBatis初始化失败");
        }
    }
    //static表明这个内容只属于对象本身，不属于任何实例,而且只会在引用mybatisutil时引用一次
    public static SqlSession getSqlSession() {
        return factory.openSession(true); // 自动提交
    }
}