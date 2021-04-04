package com.jz.exception;

import com.jz.result.CodeMsg;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@NoArgsConstructor
public class BusinessException extends RuntimeException {
    private CodeMsg codeMsg;

    public BusinessException(CodeMsg codeMsg){
        this.codeMsg = codeMsg;
    }
}
