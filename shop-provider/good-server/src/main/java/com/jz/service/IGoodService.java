package com.jz.service;

import com.jz.domain.Good;

import java.util.List;

public interface IGoodService {

    List<Good> selectByIds(List<Long> ids);

}
