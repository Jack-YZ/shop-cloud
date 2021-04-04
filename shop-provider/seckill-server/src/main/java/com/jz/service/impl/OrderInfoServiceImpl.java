package com.jz.service.impl;

import com.jz.domain.GoodVO;
import com.jz.domain.OrderInfo;
import com.jz.exception.BusinessException;
import com.jz.mapper.OrderInfoMapper;
import com.jz.redis.RedisService;
import com.jz.service.IOrderInfoService;
import com.jz.service.ISeckillGoodService;
import com.jz.service.ISeckillOrderService;
import com.jz.utils.IdGenerateUtil;
import com.jz.web.result.SeckillServerCodeMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderInfoServiceImpl implements IOrderInfoService {

    @Autowired
    private ISeckillGoodService seckillGoodService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private RedisService redisService;

    @Transactional
    @Override
    public String createSeckillOrder(Long userId, Long seckillId) {
        //库存减少
        int count = seckillGoodService.decrStock(seckillId);
        if (count == 0){//操作是否成功
            throw new BusinessException(SeckillServerCodeMsg.SECKILL_ERROR);
        }
        //增加订单详细信息OrderInfo
        String orderNo = this.createOrderInfo(userId,seckillId);
        //增加秒杀订单SeckillOrder
        //如果这个方法报错,整个事务都会回滚,避免重复下单问题
        //可以在数据库中设置索引,避免重复下单
        seckillOrderService.createSeckillOrder(userId,seckillId,orderNo);
        return orderNo;
    }

    @Override
    public OrderInfo find(String orderNo) {
        return orderInfoMapper.getByOrderNo(orderNo);
    }

    @Transactional
    @Override
    public void cancelOrderDueTimeOut(String orderNo) {
        //获取订单信息
        OrderInfo orderInfo = this.find(orderNo);
        //判断用户是否处于未支付状态
        if (orderInfo != null && orderInfo.getStatus().equals(OrderInfo.STATUS_ARREARAGE)){
            //如果是,修改状态位STATUS_TIMEOUT
            int count = orderInfoMapper.updateTimeoutStatus(orderNo, OrderInfo.STATUS_ACCOUNT_TIMEOUT);
            //更新库存,修改redis里面对应的库存-->修改状态位成功了再去操作
            if (count > 0){
                //真实库存+1
                seckillGoodService.incrStock(orderInfo.getGoodId());
                //更新redis库存
                seckillGoodService.syncStock(orderInfo.getGoodId());
                //用户超时未支付之后,已经包含了该买信息,需要删除才能再次采购 ToDo
            }else {
                //在修改订单状态前，已经有其他请求修改了订单的状态.
                throw new BusinessException(SeckillServerCodeMsg.ORDER_STATUS_ERROR);
            }
        }
    }

    @Override
    public int updataPayStatus(String orderNo, Integer status) {
        return orderInfoMapper.updatePayStatus(orderNo,status);
    }

    private String createOrderInfo(Long userId, Long seckillId) {
        //获取SeckillGoodVo对象给orderInfo
        GoodVO vo = seckillGoodService.findById(seckillId);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(userId);
        orderInfo.setCreateDate(new Date()); //创建时间
        orderInfo.setGoodCount(1); //秒杀只能购买1个
        orderInfo.setDeliveryAddrId(null); //收货地址,后续补充
        orderInfo.setGoodImg(vo.getGoodImg()); //商品图片
        orderInfo.setGoodId(vo.getId()); //商品id
        orderInfo.setGoodName(vo.getGoodName()); //商品名称
        orderInfo.setGoodPrice(vo.getGoodPrice());  //商品价格
        orderInfo.setStatus(OrderInfo.STATUS_ARREARAGE);  //商品价格
        orderInfo.setSeckillPrice(vo.getSeckillPrice());  //秒杀价格
        //订单编号不能使用自增id,因为对手每天0点和23:59:59下单,那么就获取了订单数
        //订单编号一般是有规律的,所以不建议使用随机数,否则索引作用不大
        //使用推特的雪花算法
        orderInfo.setOrderNo(IdGenerateUtil.get().nextId()+""); //订单编号

        //保存操作
        orderInfoMapper.insert(orderInfo);
        return orderInfo.getOrderNo();
    }
}
