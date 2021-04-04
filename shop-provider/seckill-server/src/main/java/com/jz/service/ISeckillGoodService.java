package com.jz.service;

import com.jz.domain.GoodVO;

import java.util.List;


public interface ISeckillGoodService {

    List<GoodVO> selectAll();

    GoodVO findById(Long seckillId);

    int decrStock(Long seckillId);

    /**
     * 同步库存
     * @param goodId
     */
    void syncStock(Long goodId);

    /**
     * 回补库存
     * @param goodId
     */
    void incrStock(Long goodId);
}
