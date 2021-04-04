package com.jz.web.result;

import com.jz.result.CodeMsg;

public class SeckillServerCodeMsg extends CodeMsg {

    private SeckillServerCodeMsg(Integer code, String msg){
        super(code,msg);
    }

    public static final SeckillServerCodeMsg LOGIN_ERROR = new SeckillServerCodeMsg(500301,"未登陆,无法进行秒杀");
    public static final SeckillServerCodeMsg SECKILL_GOOD_LIST_EMPTY = new SeckillServerCodeMsg(500302,"秒杀列表商品为空");
    public static final SeckillServerCodeMsg ILLEAGE_OP = new SeckillServerCodeMsg(500303,"非法操作");
    public static final SeckillServerCodeMsg STOCK_OVER = new SeckillServerCodeMsg(500304,"您来晚了,商品已被抢购");
    public static final SeckillServerCodeMsg SECKILL_REPEAT = new SeckillServerCodeMsg(500305,"你已下单,不能重复下单");
    public static final SeckillServerCodeMsg SECKILL_ERROR = new SeckillServerCodeMsg(500306,"秒杀失败");
    public static final SeckillServerCodeMsg VERIFYCODE_ERROR = new SeckillServerCodeMsg(500307,"验证码错误");
    public static final SeckillServerCodeMsg SECKILL_PATH_ERROR = new SeckillServerCodeMsg(500308,"秒杀地址有误");
    public static final SeckillServerCodeMsg ORDER_STATUS_ERROR = new SeckillServerCodeMsg(500309,"订单状态异常");

}