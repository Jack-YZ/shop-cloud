package com.jz.feign.hystrix;

import com.jz.feign.UserFeginApi;
import com.jz.result.Result;
import org.springframework.stereotype.Component;

@Component
public class UserFeginHystrix implements UserFeginApi{
    @Override
    public Result<Boolean> refeshTokenInRedis(String token) {
        return null;
    }
}
