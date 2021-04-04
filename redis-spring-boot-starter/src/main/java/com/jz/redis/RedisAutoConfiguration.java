package com.jz.redis;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@ConditionalOnClass({Jedis.class,JedisPool.class})
@EnableConfigurationProperties(RedisProperties.class)//初始化RedisProperties对象,并放到spring容器中
@ConditionalOnProperty(prefix = "shop.redis",name = "host")//必须配置主机地址才能起效
public class RedisAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public JedisPool jedisPool(RedisProperties redisProperties){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(redisProperties.getPoolMaxTotal());
        config.setMaxIdle(redisProperties.getPoolMaxIdle());
        config.setMaxWaitMillis(redisProperties.getPoolMaxWait()*1000);
        JedisPool jedisPool = new JedisPool(config, redisProperties.getHost(),
                redisProperties.getPort(), redisProperties.getTimeout(), redisProperties.getPassword());
        return jedisPool;
    }

    @Bean
    public RedisService redisService(){
        return new RedisService();
    }
}
