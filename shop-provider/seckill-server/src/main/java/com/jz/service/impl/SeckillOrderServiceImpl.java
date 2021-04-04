package com.jz.service.impl;


import com.jz.domain.SeckillOrder;
import com.jz.mapper.SeckillOrderMapper;
import com.jz.redis.RedisService;
import com.jz.redis.SeckillKeyPrefix;
import com.jz.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeckillOrderServiceImpl implements ISeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisService redisService;


    @Override
    public SeckillOrder findByUserIdAndSeckillId(Long userId, Long seckillId) {
        return seckillOrderMapper.findByUserIdAndSeckillId(userId, seckillId);
    }

    @Override
    public void createSeckillOrder(Long userId, Long seckillId, String orderNo) {
        SeckillOrder order = new SeckillOrder();
        order.setOrderNo(orderNo);
        order.setGoodId(seckillId);
        order.setUserId(userId);
        seckillOrderMapper.createSeckillOrder(order);
        //创建完订单之后,在redis里面打一个标记,避免重复下单
        redisService.set(SeckillKeyPrefix.SECKILL_ORDER,(userId+":"+seckillId),order);
    }
}
