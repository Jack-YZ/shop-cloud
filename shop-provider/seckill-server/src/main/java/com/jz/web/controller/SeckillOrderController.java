package com.jz.web.controller;

import com.jz.config.UserParam;
import com.jz.domain.GoodVO;
import com.jz.domain.OrderInfo;
import com.jz.domain.User;
import com.jz.exception.BusinessException;
import com.jz.mq.MQConstants;
import com.jz.mq.OrderMessage;
import com.jz.redis.MemberKeyPrefix;
import com.jz.redis.RedisService;
import com.jz.redis.SeckillKeyPrefix;
import com.jz.result.Result;
import com.jz.service.IOrderInfoService;
import com.jz.service.ISeckillGoodService;
import com.jz.utils.CookieUtil;
import com.jz.utils.VerifyCodeImgUtil;
import com.jz.web.result.SeckillServerCodeMsg;
import org.springframework.amqp.rabbit.connection.RabbitAccessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 秒杀订单相关
 */
@RestController
public class SeckillOrderController {

    @Autowired
    private RedisService redisService;
    @Autowired
    private ISeckillGoodService seckillGoodService;
    @Autowired
    private IOrderInfoService orderInfoService;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    //本地标记
    public  static ConcurrentHashMap<Long,Boolean> map = new ConcurrentHashMap<>();


    /**
     * 前端渲染二维码
     * @param response
     * @param user
     */
    @GetMapping("/getVerifyCode")
    public void getVerifyCodeImg(String uuid,String timestamp,HttpServletResponse response, @UserParam User user){
        if (user == null || StringUtils.isEmpty(uuid) || StringUtils.isEmpty(timestamp)){
            throw new BusinessException(SeckillServerCodeMsg.ILLEAGE_OP);
        }
        String verifyCode = VerifyCodeImgUtil.generateVerifyCode();
        //计算验证码结果
        Integer result = VerifyCodeImgUtil.calc(verifyCode);

        //将验证码结果存储到redis中
        redisService.set(SeckillKeyPrefix.GOOD_SECKILL_VERIFYCODE,user.getId()+"",result);

        BufferedImage bufferedImage = VerifyCodeImgUtil.createVerifyCodeImg(verifyCode);

        try {
            ImageIO.write(bufferedImage,"JPEG", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 获取秒杀路径
     * @param goodId
     * @param verifyCode
     * @param user
     * @return
     */
    @RequestMapping("/getPath")
    @ResponseBody
    public Result getPath(String uuid,Long goodId,Integer verifyCode,@UserParam User user){
        if (user == null || StringUtils.isEmpty(uuid) || StringUtils.isEmpty(verifyCode)){
            throw new BusinessException(SeckillServerCodeMsg.ILLEAGE_OP);
        }

        String path = UUID.randomUUID().toString().replace("-","");
        redisService.set(SeckillKeyPrefix.GOOD_SECKILL_PATH,user.getId()+":"+goodId,path);
        Integer result = redisService.get(user.getId() + "", SeckillKeyPrefix.GOOD_SECKILL_VERIFYCODE, Integer.class);
        if (result == null || !result.equals(verifyCode)){
            return Result.error(SeckillServerCodeMsg.VERIFYCODE_ERROR);
        }
        return Result.success(path);
    }

    /**
     * 秒杀商品
     * @return
     */
    @PostMapping("/doSeckill/{path}")
    public Result doSeckill(@PathVariable("path") String path,Long goodId, @UserParam User user,String uuid){
        //0.判断秒杀地址是否正确
        String oPath = redisService.get(user.getId() + ":" + goodId, SeckillKeyPrefix.GOOD_SECKILL_PATH, String.class);
        if(!oPath.equals(path)){
            return Result.error(SeckillServerCodeMsg.SECKILL_PATH_ERROR);
        }

        //1.判断用户是否已经登录
        if(user == null){
            return Result.error(SeckillServerCodeMsg.LOGIN_ERROR);
        }

        //2.判断已经没有库存
        Boolean b = map.get(goodId);
        if(b!=null&&b){
            return Result.error(SeckillServerCodeMsg.STOCK_OVER);
        }

        //2.判断商品是否已经到达秒杀时间
        GoodVO good = seckillGoodService.findById(goodId);
        if(good==null){
            return Result.error(SeckillServerCodeMsg.ILLEAGE_OP);
        }
        if(new Date().getTime()<good.getStartDate().getTime()){
            return Result.error(SeckillServerCodeMsg.ILLEAGE_OP);
        }
        //3.判断是否重复下单
        boolean exists = redisService.exists(SeckillKeyPrefix.SECKILL_ORDER, user.getId()+":" + goodId);
        if(exists){
            return Result.error(SeckillServerCodeMsg.SECKILL_REPEAT);
        }
        Long stock = redisService.decr(SeckillKeyPrefix.GOOD_STOCK_COUNT,""+goodId);
        if(stock <0){
            map.put(goodId,true);
            return Result.error(SeckillServerCodeMsg.STOCK_OVER);
        }
        //String orderInfo = orderInfoService.createSeckillOrder(user.getId(), goodId);
        //return Result.success(orderInfo);
        OrderMessage orderMessage = new OrderMessage();
        orderMessage.setGoodId(goodId);
        orderMessage.setUserId(user.getId());
        orderMessage.setUuid(uuid);
        rabbitTemplate.convertAndSend("", MQConstants.ORDER_PEDDING_QUEUE,orderMessage);
        return Result.success("商品抢购中,请稍后...");
    }


    /**
     * 查询商品订单详情
     * @param orderNo
     * @param user
     * @return
     */
    @RequestMapping("/orderInfo/find")
    public Result find(String orderNo, @UserParam User user){
        if (user == null || StringUtils.isEmpty(orderNo)){
            throw new BusinessException(SeckillServerCodeMsg.ILLEAGE_OP);
        }
        OrderInfo orderInfo = orderInfoService.find(orderNo);
        //如果为空,则用户非法操作
        if (orderInfo == null){
            throw new BusinessException(SeckillServerCodeMsg.ILLEAGE_OP);
        }
        //如果用户盗取别人的订单非法操作
        if (!orderInfo.getUserId().equals(user.getId())){
            throw new BusinessException(SeckillServerCodeMsg.ILLEAGE_OP);
        }
        return Result.success(orderInfo);
    }


}
