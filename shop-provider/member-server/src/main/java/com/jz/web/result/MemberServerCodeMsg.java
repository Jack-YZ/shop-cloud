package com.jz.web.result;

import com.jz.result.CodeMsg;

import java.text.MessageFormat;


public class MemberServerCodeMsg extends CodeMsg {

    private MemberServerCodeMsg(Integer code, String msg){
        super(code,msg);
    }

    public static final MemberServerCodeMsg EMPTY_ERROR = new MemberServerCodeMsg(500101,"账号密码不能为空");
    public static final MemberServerCodeMsg LOGIN_ERROR = new MemberServerCodeMsg(500102,"账号密码错误");
    public static final MemberServerCodeMsg PARAM_ERROR = new MemberServerCodeMsg(500103,"参数校验异常:{0}");

    //每次都新建一个MemberServerCodeMsg对象,都能接收到新的故障信息
    public MemberServerCodeMsg fillArgs(Object... args){
        String msg = MessageFormat.format(this.getMsg(), args);
        return new MemberServerCodeMsg(this.getCode(), msg);
    }

}
