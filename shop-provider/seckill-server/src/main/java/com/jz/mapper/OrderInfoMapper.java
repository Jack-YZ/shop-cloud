package com.jz.mapper;


import com.jz.domain.OrderInfo;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderInfoMapper {


    @Insert("INSERT into t_order_info " +
            "(order_no,user_id,good_id,good_img,good_name,good_count,good_price,seckill_price,status,create_date) " +
            "values(#{orderNo},#{userId},#{goodId},#{goodImg},#{goodName},#{goodCount},#{goodPrice},#{seckillPrice},#{status},#{createDate})")
    void insert(OrderInfo orderInfo);

    @Select("select * from t_order_info where order_no = #{orderNo}")
    OrderInfo getByOrderNo(String orderNo);

    @Update("update t_order_info set status = 1 where order_no = #{orderNo}")
    void updateOrder(String orderNo);

    @Update("update t_order_info set status = #{status},pay_date = now() where order_no= #{orderNo} and status = 0")
    int updatePayStatus(@Param("orderNo") String orderNo, @Param("status") Integer status);


    @Update("update t_order_info set status = #{status} where order_no= #{orderNo} and status = 0")
    int updateTimeoutStatus(@Param("orderNo") String orderNo, @Param("status") Integer status);
}
