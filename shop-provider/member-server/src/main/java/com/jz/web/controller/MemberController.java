package com.jz.web.controller;


import com.jz.domain.User;
import com.jz.redis.MemberKeyPrefix;
import com.jz.redis.RedisService;
import com.jz.result.Result;
import com.jz.service.IUserService;
import com.jz.utils.CookieUtil;
import com.jz.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
public class MemberController {

    @Autowired
    private IUserService userService;
    @Autowired
    private RedisService redisService;

    @GetMapping("/get")
    public Object queryMember(Long id){
        return userService.selectByPrimaryId(id);
    }

        @PostMapping("/token/login")
        public Result login(@Valid LoginVo loginVo, HttpServletResponse response){
            String token = userService.login(loginVo);
            CookieUtil.addCookie(response,CookieUtil.USER_TOKEN,token,CookieUtil.EXPIRE_TIME);
            return Result.success(token);
        }

    @GetMapping("/getCurrent")
    public Result getCurrent(HttpServletRequest request){
        String token = CookieUtil.getCookie(request, CookieUtil.USER_TOKEN);
        if (token == null){
            return Result.success(null);
        }
        User user = redisService.get(token, MemberKeyPrefix.USER_TOKEN, User.class);
        return Result.success(user);
    }
}
