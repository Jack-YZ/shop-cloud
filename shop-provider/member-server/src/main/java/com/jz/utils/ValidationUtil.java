package com.jz.utils;


import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号码校验的工具类
 */
public class ValidationUtil {

    public static boolean validate(String value){
        //首先判断是否为空
        if(StringUtils.isEmpty(value)){
            return false;
        }
        //验证
        Matcher matcher = Pattern.compile("1\\d{10}").matcher(value);
        return matcher.matches();
    }

}
