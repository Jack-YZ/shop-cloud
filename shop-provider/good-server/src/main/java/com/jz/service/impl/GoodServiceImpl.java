package com.jz.service.impl;


import com.jz.domain.Good;
import com.jz.mapper.GoodMapper;
import com.jz.service.IGoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodServiceImpl implements IGoodService {

    @Autowired
    private GoodMapper goodMapper;


    @Override
    public List<Good> selectByIds(List<Long> ids) {
        return goodMapper.selectByIds(ids);
    }
}
