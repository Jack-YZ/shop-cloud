package com.jz.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class ErrorResponseJSONFilter extends ZuulFilter {
    public static final String DEFAULT_ERR_MSG = "系统繁忙,请稍后再试";
    @Override
    public String filterType() {
        return "error";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {

        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();

        try {
            HttpServletRequest request = ctx.getRequest();
            String rateLimitExceeded = (String) ctx.get("rateLimitExceeded");
            String message = null;
            if("true".equals(rateLimitExceeded)){
                message = "您请求速度太快了，请稍后再试.";
            }
            if(StringUtils.isBlank(message)){
                message = DEFAULT_ERR_MSG;
            }
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
            ctx.getResponse().getWriter().write("{\"code\":500,\"msg\":\""+message+"\"}");
        } catch (Exception e) {
            e.printStackTrace();
            String error = "Error during filtering[ErrorFilter]";
            try {
                ctx.getResponse().getWriter().write("{\"code\":500,\"msg\":\""+error+"\"}");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }
}
