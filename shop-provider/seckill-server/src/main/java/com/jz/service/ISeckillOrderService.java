package com.jz.service;


import com.jz.domain.SeckillOrder;

public interface ISeckillOrderService {

    /**
     * 根据用户id和秒杀商品id查询订单
     * @param userId
     * @param seckillId
     * @return
     */
    SeckillOrder findByUserIdAndSeckillId(Long userId, Long seckillId);

    /**
     * 创建秒杀订单
     * @param userId
     * @param seckillId
     * @param orderNo
     */
    void createSeckillOrder(Long userId, Long seckillId, String orderNo);
}
