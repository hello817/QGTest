package com.dorm.config;

import com.dorm.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//注册拦截器,告诉拦截器要拦截哪些请求
@Configuration
public class WebConfig implements WebMvcConfigurer/*别忘了继承父类，孩子*/ {
    @Autowired
    private AuthInterceptor authInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册流程
        registry.addInterceptor(authInterceptor)//要用谁拦截请求？，即登记拦截器
                .addPathPatterns("/**")//凡是/开头（也就是除了下面排除的路径以外全都拦截来验证，这样也就不用每个接口都写token验证了，原来如此）
                .excludePathPatterns(
                        "/users/public/login",//登录不拦
                        "/users/public"
                );
    }
}
