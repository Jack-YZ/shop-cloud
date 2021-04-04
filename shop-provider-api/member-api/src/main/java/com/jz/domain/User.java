package com.jz.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
public class User implements Serializable{

    private Long id;
    private String nickname;//昵称
    private String head;//头像
    @JsonIgnore//不需要解析到前台
    private String password;//密码
    @JsonIgnore
    private String salt;//盐
    @JsonIgnore
    private Date registerDate;//注册时间
    @JsonIgnore
    private Date lastLoginDate;//最后登录时间
    @JsonIgnore
    private Integer loginCount;//登录次数
}
