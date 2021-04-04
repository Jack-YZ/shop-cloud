package com.jz.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static final String USER_TOKEN = "userToken";
    public static final int EXPIRE_TIME = 1800;

    public static void addCookie(HttpServletResponse response, String cookieName, String cookieValue, int expireTime){
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setMaxAge(expireTime);
        response.addCookie(cookie);
    }

    public static String getCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if(cookies ==null ||cookies.length==0){
            return null;
        }
        for(Cookie cookie:cookies){
            if(cookie.getName().equals(cookieName)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
