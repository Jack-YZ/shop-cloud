package com.jz.vo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
@Setter@Getter
public class LoginVo implements Serializable {
    @Pattern(regexp = "1[3456789]\\d{9}",message = "手机号码格式不正确")
    private String mobile;
    @NotEmpty
    private String password;
}
