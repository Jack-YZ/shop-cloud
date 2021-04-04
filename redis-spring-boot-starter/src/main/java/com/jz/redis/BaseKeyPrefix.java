package com.jz.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class BaseKeyPrefix implements KeyPrefix {

    private String prefix;
    private int expireSeconds;

    public BaseKeyPrefix(){}

    public BaseKeyPrefix(String prefix, int expireSeconds){
        this.prefix  = prefix;
        this.expireSeconds = expireSeconds;
    }

    @Override
    public String getPrefix() {
        return this.getClass().getSimpleName()+":"+this.prefix + ":";
    }

    @Override
    public int getExpireSeconds() {
        return this.expireSeconds;
    }


}
