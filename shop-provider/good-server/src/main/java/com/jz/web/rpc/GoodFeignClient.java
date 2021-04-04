package com.jz.web.rpc;

import com.jz.domain.Good;
import com.jz.result.Result;
import com.jz.feign.GoodFeignApi;
import com.jz.service.IGoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GoodFeignClient implements GoodFeignApi {

    @Autowired
    private IGoodService goodService;

    @Override
    public Result<List<Good>> queryByIds(List<Long> ids) {
        return Result.success(goodService.selectByIds(ids));
    }
}
