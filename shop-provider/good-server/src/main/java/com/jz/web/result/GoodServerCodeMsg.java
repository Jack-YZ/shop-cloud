package com.jz.web.result;

import com.jz.result.CodeMsg;

import java.text.MessageFormat;


public class GoodServerCodeMsg extends CodeMsg {

    private GoodServerCodeMsg(Integer code, String msg){
        super(code,msg);
    }

    public static final GoodServerCodeMsg GOOD_LIST_EMPTY = new GoodServerCodeMsg(500201,"查询商品为空");


}
