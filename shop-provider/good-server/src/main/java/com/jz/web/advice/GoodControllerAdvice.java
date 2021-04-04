package com.jz.web.advice;

import com.jz.exception.CommonControllerAdvice;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@ResponseBody
public class GoodControllerAdvice extends CommonControllerAdvice {
}
