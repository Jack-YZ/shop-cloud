package com.jz.exception;

import com.jz.result.CodeMsg;
import com.jz.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 作为基类给后面继承,所以暂时不需要@ControllerAdvice
 */
@ResponseBody
public class CommonControllerAdvice {

    /**
     * 自定义异常
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public Result<CodeMsg> handleBussinessException(BusinessException e){
        return Result.error(e.getCodeMsg());
    }

    //系统异常
    @ExceptionHandler(Exception.class)
    public Result<CodeMsg> handleDefaultException(Exception e){
        e.printStackTrace();
        return Result.defaultError();
    }
}
