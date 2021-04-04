package com.jz.filter;


import com.jz.feign.UserFeginApi;
import com.jz.result.Result;
import com.jz.utils.CookieUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 设定的token的有效时间是半个小时,当用户访问的时候,我们需要延长token有效时间
 * 所以当用户跳转页面的时候,延长有效时间
 */
@Component
public class TokenLoginFilter extends ZuulFilter {

    @Autowired
    private UserFeginApi userFeginApi;

    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        //当页面跳转的时候运行,判断请求是否含有token信息,如果有那么执行方法,没有则直接跳过
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        //查询是否有token
        String token = CookieUtil.getCookie(request,CookieUtil.USER_TOKEN);
        return !StringUtils.isEmpty(token);
    }

    @Override
    public Object run(){
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        HttpServletResponse response = currentContext.getResponse();
        //查询token
        String token = CookieUtil.getCookie(request,CookieUtil.USER_TOKEN);
        //刷新redis的信息
        //刷新浏览器token
        Result<Boolean> result = userFeginApi.refeshTokenInRedis(token);
        //如果刷新redis成功之后才能刷新浏览器token
        if (result != null && !result.hasError() && result.getData()) {
            CookieUtil.addCookie(response, CookieUtil.USER_TOKEN, token, CookieUtil.EXPIRE_TIME);
        }
        return null;
    }
}
