package com.jz.mapper;

import com.jz.domain.SeckillGood;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SeckillMapper {

    @Select("select * from t_seckill_goods")
    List<SeckillGood> list();

    @Select("select * from t_seckill_goods where id = #{seckillId}")
    SeckillGood find(Long seckillId);

    //返回值int表示更新数据的条数
    @Update("update t_seckill_goods set stock_count = stock_count - 1 where id = #{seckillId} and stock_count > 0")
    int decrStock(Long seckillId);

    @Select("select stock_count from t_seckill_goods where id = #{seckillId}")
    Integer synchroizRedisStockCount(Long seckillId);

    //返回值int表示更新数据的条数
    @Update("update t_seckill_goods set stock_count = stock_count + 1 where id = #{seckillId}")
    Integer incrStock(Long seckillId);
}
