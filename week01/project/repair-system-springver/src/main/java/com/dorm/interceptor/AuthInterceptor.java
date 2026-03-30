package com.dorm.interceptor;

import com.dorm.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;
    //覆写controller执行前的prehandle
    @Override
    public boolean preHandle(HttpServletRequest request/*截取http请求*/,HttpServletResponse response,Object handler)
    throws Exception/**/{
        System.out.println("拦截请求: " + request.getRequestURI());  // 加这行
        //从请求头（request）获取token
        String token = request.getHeader("Authorization");
        //检查token是否存在
        if(token == null||token.isEmpty()){
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");//设置响应内容格式
            response.getWriter().write("{\"code\":401,\"message\":\"未登录\"}");
            return false;
        }
        //去掉bearer前缀
        if(token.startsWith("Bearer ")) {
            token = token.substring(7);//token取第七位之后的值，即过滤掉bearer前缀
        }
        //验证token是否有效
        if(!jwtUtils.validateTokeb(token)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");//设置响应内容格式
            response.getWriter().write("{\"code\":401,\"message\":\"token无效或已过期\"}");
            return false;
        }
        //将用户id存入请求属性供controller使用
        long userId = jwtUtils.getUserIdInToken(token);
        request.setAttribute("userId",userId);
        //验证全部通过时放行，进入controller层执行
        return true;
    }
}
