package com.dorm.utils;
//token生成
import com.dorm.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.AccessFlag;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    @Autowired
    private JwtConfig jwtConfig;
    /**
     * token信息
     * userId 用户id
     *account 账号
     *role 角色
     *返回token字符串
     *这里最好还是把密码过滤掉，只返回需要的信息就好
     * */
    //token生成
    public String generateToken(long userId,String account,String role){
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId",userId);
        claims.put("account",account);
        claims.put("role",role);
        return  Jwts.builder()
                .setClaims(claims)          //用户信息
                .setIssuedAt(new Date())    //签发时间
                .setExpiration(new Date(System.currentTimeMillis()+jwtConfig.getExpiration()))
                .signWith(SignatureAlgorithm.HS256, jwtConfig.getSecret())//签名算法和签名密钥
                .compact();
    }
    //token验证
    public boolean validateTokeb(String token){
        try{
            parseToken(token);
            return true;
        }catch(ExpiredJwtException e){
            System.out.println("Token已过期");
        }catch (UnsupportedJwtException e){
            System.out.println("不支持的Token");
        }catch (MalformedJwtException e){
            System.out.println("Token格式错误");
        }catch(SignatureException e){
            System.out.println("签名错误");
        }catch(IllegalArgumentException e){
            System.out.println("Token为空");
        }
        return  false;
    }
    //解析token
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtConfig.getSecret())
                .parseClaimsJws(token)
                .getBody();
    }
    //获取token信息
    //获取id
    public long getUserIdInToken(String token){
        Claims claims = parseToken(token);
        Integer userIdInt = claims.get("userId", Integer.class);
        Long userId = userIdInt.longValue();
        return userId;
    }
    //获取账号
    public String getAccountInToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("account", String.class);
    }
    //获取身份
    public String getRoleInToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("role",String.class);
    }
}