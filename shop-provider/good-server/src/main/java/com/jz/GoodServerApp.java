package com.jz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableCircuitBreaker //使用断路器,在对应的方法上加入@HystrixCommand注解实现断路器功能，当service方法对应的服务发生异常的时候，会跳转到serviceFallback方法执行
@EnableFeignClients //用于告诉框架扫描所有通过注解@FeignClient定义的feign客户端
public class GoodServerApp {
    public static void main( String[] args ) {
        SpringApplication.run(GoodServerApp.class,args);
    }
}
