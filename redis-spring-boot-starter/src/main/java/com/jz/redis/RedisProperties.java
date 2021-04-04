package com.jz.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter@Setter
@ConfigurationProperties(prefix = "shop.redis")
public class RedisProperties {
    private String host = "localhost";

    private int port = 6379;

    private String password;

    private int timeout = 10;

    private int poolMaxTotal = 500;

    private int poolMaxIdle = 500;

    private int poolMaxWait = 500;
}
