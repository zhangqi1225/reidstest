package com.example.demo.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by zhang on 2017/11/11.
 */

@Data
@Configuration
@PropertySource(value = {"classpath:redis.properties"},ignoreResourceNotFound = true)
@ConfigurationProperties(prefix = "redis.pool")
public class RedisConfig {

    private String host;
    private int port;
    private int timeout;
    private int database;
    private String cyberArkKey;
    private String password;
    private int maxActive;
    private int maxIdle;
    private int minIdle;
    private int maxWait;
    private Boolean testOnBorrow;
    private Boolean testOnReturn;
}
