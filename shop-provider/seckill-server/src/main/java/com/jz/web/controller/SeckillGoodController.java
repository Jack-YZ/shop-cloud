package com.jz.web.controller;

import com.jz.domain.Good;
import com.jz.domain.GoodVO;
import com.jz.redis.RedisService;
import com.jz.redis.SeckillKeyPrefix;
import com.jz.result.Result;
import com.jz.service.ISeckillGoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Map;

/**
 * 秒杀商品相关
 */
@RestController
public class SeckillGoodController {

    @Autowired
    private ISeckillGoodService seckillGoodService;
    @Autowired
    private RedisService redisService;

    /**
     * 查询秒杀商品列表---优化前
     * @return
     */
    @GetMapping("/toList1")
    public Result toList1(){
        return Result.success(seckillGoodService.selectAll());
    }

    /**
     * 查询秒杀商品详情---优化前
     * @param seckillId
     * @return
     */
    @GetMapping("/find1")
    public Result find1(Long seckillId){
        GoodVO seckillGood = seckillGoodService.findById(seckillId);
        return Result.success(seckillGood);
    }

    //--------------------------------------------------------------------------------

    /**
     * 查询秒杀商品列表---优化后,进行商品预热
     * @return
     */
    @GetMapping("/toList")
    public Result toList(){
        Map<String, GoodVO> goodVOMap = redisService.hgetAll(SeckillKeyPrefix.GOOD_TO_LIST, "", GoodVO.class);
        return Result.success(new ArrayList<>(goodVOMap.values()));
    }



    /**
     * 查询秒杀商品详情---优化后
     * @param seckillId
     * @return
     */
    @GetMapping("/find")
    public Result find(Long seckillId){
        GoodVO goodVO = redisService.hget(SeckillKeyPrefix.GOOD_TO_LIST, "", seckillId + "", GoodVO.class);
        return Result.success(goodVO);
    }
}
