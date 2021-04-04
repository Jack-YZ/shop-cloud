package com.jz.redis;
//定义一个接口规范
public interface KeyPrefix {
    String getPrefix();
    int getExpireSeconds();
}