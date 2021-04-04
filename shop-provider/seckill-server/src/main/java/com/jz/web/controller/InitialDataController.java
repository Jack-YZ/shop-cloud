package com.jz.web.controller;

import com.jz.domain.GoodVO;
import com.jz.redis.RedisService;
import com.jz.redis.SeckillKeyPrefix;
import com.jz.result.Result;
import com.jz.service.ISeckillGoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class InitialDataController {
    @Autowired
    private RedisService redisService;
    @Autowired
    private ISeckillGoodService seckillGoodService;

    @GetMapping("/initialData")
    public Result initialData(){
        List<GoodVO> goodVOS = seckillGoodService.selectAll();
        for (GoodVO goodVO : goodVOS) {
            redisService.hset(SeckillKeyPrefix.GOOD_TO_LIST,"",goodVO.getId()+"",goodVO);
            redisService.set(SeckillKeyPrefix.GOOD_STOCK_COUNT,""+goodVO.getId(),goodVO.getStockCount());
        }
        return Result.success("初始化数据成功");
    }
}
