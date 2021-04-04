package com.jz.web.advice;

import com.jz.exception.CommonControllerAdvice;
import com.jz.result.CodeMsg;
import com.jz.result.Result;
import com.jz.web.result.MemberServerCodeMsg;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
public class MemberControllerAdvice extends CommonControllerAdvice {
    //处理本服务的自定义的异常-例如@Valid验证参数异常
    @ExceptionHandler(BindException.class)
    public Result<CodeMsg> handleBindException(BindException e){
        //获取异常信息
        String msg = e.getAllErrors().get(0).getDefaultMessage();
        //拼接异常信息
        return Result.error(MemberServerCodeMsg.PARAM_ERROR.fillArgs(msg));
    }


}
