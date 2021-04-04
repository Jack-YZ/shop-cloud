package com.jz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableEurekaClient
@EnableConfigServer //分布式配置中心服务端
public class ConfigServerApp {
    public static void main( String[] args ) {
        SpringApplication.run(ConfigServerApp.class,args);
    }
}
