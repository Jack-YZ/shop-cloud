package com.jz.mq;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrderMessage implements Serializable{
    private Long userId;

    private Long goodId;

    private String uuid;

}
