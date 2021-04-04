package com.jz.result;

import lombok.*;

import java.io.Serializable;

@Setter@Getter@AllArgsConstructor@NoArgsConstructor
public class Result<T> implements Serializable{
    //默认成功码和信息
    public static final Integer SUCCESS_CODE = 200;
    public static final String SUCCESS_MESSAGE = "操作成功";
    //默认错误码和信息
    public static final Integer ERROR_CODE = 500000;
    public static final String ERROR_MESSAGE = "系统繁忙,请稍后再试";

    private Integer code;
    private String msg;
    private T data;

    public static <T> Result success(String msg,T data){
        return new Result<>(SUCCESS_CODE,msg,data);
    }

    public static <T> Result success(T data){
        return new Result<>(SUCCESS_CODE,SUCCESS_MESSAGE,data);
    }

    public static <T> Result<CodeMsg> defaultError(){
        return new Result<>(ERROR_CODE,ERROR_MESSAGE,null);
    }

    public static Result<CodeMsg> error(CodeMsg codeMsg) {
        return new Result<>(codeMsg.getCode(),codeMsg.getMsg(),null);
    }

    /**
     * 判断是否出现异常,this表示调用此方法对象
     * @return
     */
    public boolean hasError() {
        return !this.getCode().equals(SUCCESS_CODE);
    }
}
