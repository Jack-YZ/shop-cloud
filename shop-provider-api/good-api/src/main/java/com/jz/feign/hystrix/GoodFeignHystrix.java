package com.jz.feign.hystrix;

import com.jz.domain.Good;
import com.jz.feign.GoodFeignApi;
import com.jz.result.Result;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class GoodFeignHystrix implements GoodFeignApi{
    @Override
    public Result<List<Good>> queryByIds(List<Long> ids) {
        return null;
    }
}
