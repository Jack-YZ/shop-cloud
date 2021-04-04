package com.jz.mapper;


import com.jz.domain.SeckillGood;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SeckillGoodMapper {

    @Select("select * from t_seckill_goods")
    List<SeckillGood> selectAll();

    @Select("select * from t_seckill_goods where id = #{id}")
    SeckillGood selectByPrimaryKey(Long id);

    @Update("update t_seckill_goods set stock_count =stock_count -1 where id = #{goodId} and stock_count>0")
    int reduceStockCount(Long goodId);

    @Select("select stock_count from t_seckill_goods where id = #{goodId}")
    Integer getGoodStock(Long goodId);

    @Update("update t_seckill_goods set stock_count =stock_count +1 where id = #{goodId}")
    void incrStock(Long goodId);
}
