package com.jz.redis;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class MemberKeyPrefix extends BaseKeyPrefix{


    public MemberKeyPrefix(String prefix, int expireSeconds) {
        super(prefix, expireSeconds);
    }

    public static final MemberKeyPrefix USER_TOKEN =
            new MemberKeyPrefix("userToken",1800);

    public static final MemberKeyPrefix GET_BY_ID =
            new MemberKeyPrefix("getById",30*24*3600);

}
