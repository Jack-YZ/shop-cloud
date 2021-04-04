package com.jz.utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5加密工具类
 */
public class MD5Util {

    public static final String salt = "abcd";

    public static String md5(String value){
        return DigestUtils.md5Hex(value);
    }

    public static String inputPassToFormPass(String value){
        return md5(""+salt.charAt(0)+salt.charAt(1)+value+salt.charAt(2)+salt.charAt(3));
    }

    public static String formPassToDbPass(String value,String salt){
        return md5(""+salt.charAt(0)+salt.charAt(1)+value+salt.charAt(2)+salt.charAt(3));
    }

    public static String inputPassToDbPass(String value){
        return md5(""+salt.charAt(0)+salt.charAt(1)+value+salt.charAt(2)+salt.charAt(3));
    }

    public static void main(String[] args) {
        String inputPass = inputPassToFormPass("1111");
        String dbPass = formPassToDbPass(inputPass,"abcd");
        System.out.println(inputPass);
        System.out.println(dbPass);

    }


}
