package com.jz.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
//订单详情类
public class OrderInfo implements Serializable {
    public static final Integer STATUS_ARREARAGE = 0;//未付款
    public static final Integer STATUS_ACCOUNT_PAID = 1;//已付款
    public static final Integer STATUS_ACCOUNT_CANCEL = 2;//手动取消订单
    public static final Integer STATUS_ACCOUNT_TIMEOUT = 3;//超时取消订单
    private Long id;
    private String orderNo;
    private Long userId;
    private Long goodId;
    private Long deliveryAddrId;
    private String goodName;
    private String goodImg;
    private Integer goodCount;
    private BigDecimal goodPrice;
    private BigDecimal seckillPrice;
    private Integer status = STATUS_ARREARAGE;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GTM+8")
    private Date createDate;
    private Date payDate;
}