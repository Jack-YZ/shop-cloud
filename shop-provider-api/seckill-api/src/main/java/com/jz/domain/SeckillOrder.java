package com.jz.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
//秒杀订单类,防止秒杀商品重复下单
public class SeckillOrder implements Serializable {
    private Long id;
    private String orderNo;//订单标号
    private Long userId;
    private Long goodId;
}