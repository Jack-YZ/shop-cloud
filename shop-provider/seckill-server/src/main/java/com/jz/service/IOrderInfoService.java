package com.jz.service;


import com.jz.domain.OrderInfo;

public interface IOrderInfoService {

    /**
     * 下单
     * @param userId
     * @param seckillId
     * @return
     */
    String createSeckillOrder(Long userId, Long seckillId);

    /**
     * 查询订单信息
     * @param orderNo
     * @return
     */
    OrderInfo find(String orderNo);


    /**
     * 付款成功后修改订单状态
     * @param orderNo
     * @param status
     * @return
     */
    int updataPayStatus(String orderNo, Integer status);

    /**
     * 处理超时订单
     * @param seckillId
     * @param orderNo
     */
    void cancelOrderDueTimeOut(String orderNo);
}
