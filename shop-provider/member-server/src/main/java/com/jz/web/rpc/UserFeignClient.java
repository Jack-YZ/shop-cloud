package com.jz.web.rpc;

import com.jz.feign.UserFeginApi;
import com.jz.result.Result;
import com.jz.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserFeignClient implements UserFeginApi {
    @Autowired
    private IUserService userService;

    /**
     * 刷新redis中token的失效时间
     * @param token
     * @return
     */
    @Override
    public Result<Boolean> refeshTokenInRedis(String token) {
        return Result.success(userService.refeshToken(token));
    }
}
