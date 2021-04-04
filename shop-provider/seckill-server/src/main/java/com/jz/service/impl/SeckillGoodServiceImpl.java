package com.jz.service.impl;

import com.jz.domain.Good;
import com.jz.domain.GoodVO;
import com.jz.domain.SeckillGood;
import com.jz.exception.BusinessException;
import com.jz.mapper.SeckillGoodMapper;
import com.jz.mq.MQConstants;
import com.jz.redis.RedisService;
import com.jz.redis.SeckillKeyPrefix;
import com.jz.result.Result;
import com.jz.feign.GoodFeignApi;
import com.jz.service.ISeckillGoodService;
import com.jz.web.result.SeckillServerCodeMsg;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SeckillGoodServiceImpl implements ISeckillGoodService {

    @Autowired
    private SeckillGoodMapper seckillGoodMapper;
    @Autowired
    private RedisService redisService;
    @Autowired
    private GoodFeignApi goodFeignApi;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 查询秒杀商品,内存做聚合
     * @return
     */
    @Override
    public List<GoodVO> selectAll() {
        //单表查询,查完之后在内存中在聚合(否则分库分表之后链表的语句就会很麻烦)
        List<SeckillGood> seckillGoods = seckillGoodMapper.selectAll();
        return createGoodVOS(seckillGoods);
    }

    private List<GoodVO> createGoodVOS(List<SeckillGood> seckillGoods) {
        Map<Long,SeckillGood> sgMap = new HashMap<>();
        for (SeckillGood seckillGood:seckillGoods) {
            sgMap.put(seckillGood.getGoodId(),seckillGood);
        }
        Set<Long> set = sgMap.keySet();
        List<Long> goodIds = new ArrayList<>(set);
        //获取商品信息,rpc远程调用
        Result<List<Good>> result = goodFeignApi.queryByIds(goodIds);
        if (result == null || result.hasError()){//降级或者出现错误
            throw new BusinessException(SeckillServerCodeMsg.SECKILL_GOOD_LIST_EMPTY);
        }
        List<Good> goods = result.getData();
        List<GoodVO> voList = new ArrayList<>();
        for (Good good :goods){
            SeckillGood sgGood = sgMap.get(good.getId());
            GoodVO vo = new GoodVO();
            vo.setGoodTitle(good.getGoodTitle());
            vo.setGoodStock(good.getGoodStock());
            vo.setGoodPrice(good.getGoodPrice());
            vo.setGoodName(good.getGoodName());
            vo.setGoodImg(good.getGoodImg());
            vo.setGoodDetail(good.getGoodDetail());
            vo.setStockCount(sgGood.getStockCount());
            vo.setSeckillPrice(sgGood.getSeckillPrice());
            vo.setStartDate(sgGood.getStartDate());
            vo.setEndDate(sgGood.getEndDate());
            vo.setId(good.getId());
            voList.add(vo);
        }
        return voList;
    }


    /**
     * 查询秒杀商品详情
     * @param seckillId
     * @return
     */
    @Override
    public GoodVO findById(Long seckillId) {
        SeckillGood seckillGood = seckillGoodMapper.selectByPrimaryKey(seckillId);
        List<SeckillGood> list = new ArrayList<>();
        list.add(seckillGood);
        List<GoodVO> voList = createGoodVOS(list);
        return voList.get(0);
    }

    /**
     * 下单成功,减少商品库存
     * @param seckillId
     * @return
     */
    @Override
    public int decrStock(Long seckillId) {
        return seckillGoodMapper.reduceStockCount(seckillId);
    }

    /**
     * 同步库存
     * @param goodId
     */
    @Override
    public void syncStock(Long goodId) {
        Integer goodStock = seckillGoodMapper.getGoodStock(goodId);
        if (goodStock>0){
            redisService.set(SeckillKeyPrefix.GOOD_STOCK_COUNT,goodId+"",goodStock);
        }
        //发布取消本地标识的消息
        rabbitTemplate.convertAndSend(MQConstants.SECKILL_OVER_SIGN_PUBSUB_EX,"",goodId);
    }

    /**
     * 回补库存
     * @param goodId
     */
    @Override
    public void incrStock(Long goodId) {
        seckillGoodMapper.incrStock(goodId);
    }
}
