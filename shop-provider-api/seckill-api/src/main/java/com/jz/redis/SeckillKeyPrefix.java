package com.jz.redis;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class SeckillKeyPrefix extends BaseKeyPrefix{


    public SeckillKeyPrefix(String prefix, int expireSeconds) {
        super(prefix, expireSeconds);
    }

    public static final SeckillKeyPrefix SECKILL_ORDER = new SeckillKeyPrefix("seckillOrder",1800);
    public static final SeckillKeyPrefix GOOD_STOCK_COUNT = new SeckillKeyPrefix("goodStockCount",-1);
    public static final SeckillKeyPrefix GOOD_SECKILL_PATH = new SeckillKeyPrefix("path",180);
    public static final SeckillKeyPrefix GOOD_SECKILL_VERIFYCODE = new SeckillKeyPrefix("seckillVerifyCode",180);
    public static final SeckillKeyPrefix GOOD_TO_LIST = new SeckillKeyPrefix("goodToList",-1);

}
