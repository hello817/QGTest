package com.dorm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")//读取配置文件中"jwt"的部分
public class JwtConfig {
    private String secret;//密钥
    private long expiration;

    //getter/setter
    public String getSecret() {return secret;}
    public void setSecret(String secret) {this.secret = secret;}

    public long getExpiration() {return expiration;}
    public void setExpiration(long expiration) {this.expiration = expiration;}

}
