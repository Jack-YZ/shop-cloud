package com.jz.service.impl;

import com.jz.domain.User;
import com.jz.exception.BusinessException;
import com.jz.mapper.UserMapper;
import com.jz.redis.MemberKeyPrefix;
import com.jz.redis.RedisService;
import com.jz.service.IUserService;
import com.jz.utils.MD5Util;
import com.jz.vo.LoginVo;
import com.jz.web.result.MemberServerCodeMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisService redisService;

    @Override
    public User selectByPrimaryId(Long id) {
        return userMapper.selectById(id);
    }


    /**
     * token更新
     * @param token
     * @return
     */
    @Override
    public Boolean refeshToken(String token) {
        if(!redisService.exists(MemberKeyPrefix.USER_TOKEN,token)){
            return false;
        }
        redisService.expire(MemberKeyPrefix.USER_TOKEN,token,MemberKeyPrefix.USER_TOKEN.getExpireSeconds());
        return true;
    }

    /**
     * 登录
     * @param loginVo
     * @return
     */
    @Override
    public String login(LoginVo loginVo) {
        if (StringUtils.isEmpty(loginVo.getMobile())){
            throw new BusinessException(MemberServerCodeMsg.EMPTY_ERROR);
        }
        //根据id去数据库获取信息
        User user = userMapper.selectById(Long.valueOf(loginVo.getMobile()));
        //如果用户为空,账号错误
        if (user == null){
            throw new BusinessException(MemberServerCodeMsg.LOGIN_ERROR);
        }
        //如果账号正常,对密码进行校验
        String encodePassword = MD5Util.formPassToDbPass(loginVo.getPassword(), user.getSalt());
        if (!user.getPassword().equals(encodePassword)){
            throw new BusinessException(MemberServerCodeMsg.LOGIN_ERROR);
        }
        //如果账号和密码全部正确了,那么返回token信息
        return createToken(user);
    }

    /**
     * 创建token
     * @param user
     * @return
     */
    private String createToken(User user) {
        //为了避免重复,采用随机数方式存储
        String token = UUID.randomUUID().toString().replace("-","");
        //将信息存储到redis数据库
        redisService.set(MemberKeyPrefix.USER_TOKEN,token,user);
        return token;
    }


}
