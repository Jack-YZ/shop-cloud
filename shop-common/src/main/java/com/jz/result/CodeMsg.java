package com.jz.result;

import lombok.*;

import java.io.Serializable;

@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class CodeMsg implements Serializable {
    private Integer code;
    private String msg;
}