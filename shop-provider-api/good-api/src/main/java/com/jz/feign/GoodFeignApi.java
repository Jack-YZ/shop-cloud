package com.jz.feign;

import com.jz.domain.Good;
import com.jz.feign.hystrix.GoodFeignHystrix;
import com.jz.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="good-server",fallback= GoodFeignHystrix.class)
public interface GoodFeignApi{

    @RequestMapping("/api/good/queryByIds")
    Result<List<Good>> queryByIds(@RequestParam("ids") List<Long> ids);
}
