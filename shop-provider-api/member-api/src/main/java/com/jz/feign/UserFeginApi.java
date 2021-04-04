package com.jz.feign;

import com.jz.feign.hystrix.UserFeginHystrix;
import com.jz.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "member-server",fallback = UserFeginHystrix.class)
public interface UserFeginApi {


    /**
     * 刷新redis中token的失效时间
     * @param token
     * @return
     */
    @RequestMapping("/api/token/refesh")
    Result<Boolean> refeshTokenInRedis(@RequestParam("token") String token);
}
