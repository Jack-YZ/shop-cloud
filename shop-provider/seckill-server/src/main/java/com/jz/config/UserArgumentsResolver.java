package com.jz.config;

import com.jz.domain.User;
import com.jz.redis.MemberKeyPrefix;
import com.jz.redis.RedisService;
import com.jz.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Component
public class UserArgumentsResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private RedisService redisService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //为了区分注入还是作为参数接收,贴注解
        return parameter.hasParameterAnnotation(UserParam.class) && parameter.getParameterType() == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        //获取request对象
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        //获取cookie(token)
        String token = CookieUtil.getCookie(request, CookieUtil.USER_TOKEN);
        if (StringUtils.isEmpty(token)){
            return null;
        }
        User user = redisService.get(token,MemberKeyPrefix.USER_TOKEN,User.class);
        return user;
    }
}
