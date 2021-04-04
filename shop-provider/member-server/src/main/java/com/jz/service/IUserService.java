package com.jz.service;

import com.jz.domain.User;
import com.jz.vo.LoginVo;

public interface IUserService {

    String login(LoginVo loginVo);

    User selectByPrimaryId(Long id);

    Boolean refeshToken(String token);
}
