package com.jz.mq;

import com.jz.domain.OrderInfo;
import com.jz.exception.BusinessException;
import com.jz.service.IOrderInfoService;
import com.jz.service.ISeckillGoodService;
import com.jz.service.ISeckillOrderService;
import com.jz.web.controller.SeckillOrderController;
import com.jz.web.result.SeckillServerCodeMsg;
import com.netflix.ribbon.proxy.annotation.Http;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class OrderMQListener {
    @Autowired
    private ISeckillGoodService seckillGoodService;
    @Autowired
    private IOrderInfoService orderInfoService;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 初始队列
     * @param orderMessage
     * @param deliveryTag
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queuesToDeclare = {
            @Queue(MQConstants.ORDER_PEDDING_QUEUE)
    })
    public void handlerOrderPeddingQueue(@Payload OrderMessage orderMessage, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", orderMessage.getUuid());
        map.put("goodId", orderMessage.getGoodId());
        try {
            String orderNo = orderInfoService.createSeckillOrder(orderMessage.getUserId(), orderMessage.getGoodId());
            map.put("code", 200);
            map.put("orderNo", orderNo);
            rabbitTemplate.convertAndSend(MQConstants.ORDER_RESULT_EXCHANGE, MQConstants.ORDER_RESULT_SUCCESS_KEY, map);
        } catch (Exception ex) {
            ex.printStackTrace();
            map.put("code", SeckillServerCodeMsg.SECKILL_ERROR.getCode());
            map.put("msg", SeckillServerCodeMsg.SECKILL_ERROR.getMsg());
            rabbitTemplate.convertAndSend(MQConstants.ORDER_RESULT_EXCHANGE, MQConstants.ORDER_RESULT_FAIL_KEY, map);
        } finally {
            //手动接收
            channel.basicAck(deliveryTag, false);
        }
    }


    /**
     * 秒杀订单失败
     * @param map
     * @param deliveryTag
     * @param channel
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(MQConstants.ORDER_RESULT_FAIL_QUEUE),
            exchange = @Exchange(name = MQConstants.ORDER_RESULT_EXCHANGE, type = "topic"),
            key = MQConstants.ORDER_RESULT_FAIL_KEY))
    public void handlerOrderFailMsg(@Payload Map<String, Object> map, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        try {
            Long goodId = (Long) map.get("goodId");
            seckillGoodService.syncStock(goodId);
        }catch (Exception e){
            e.printStackTrace();
            //库存回补未成功,可以将此消息放到死信队列中,发消息给客服,让客服手动处理
        }finally {
            channel.basicAck(deliveryTag, false);
        }
    }


    /**
     * 创建超时订单队列
     * @return
     */
    @Bean
    public org.springframework.amqp.core.Queue orderDelayQueue() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", MQConstants.DELAY_EXCHANGE);
        arguments.put("x-dead-letter-routing-key", MQConstants.ORDER_DELAY_KEY);
        arguments.put("x-message-ttl", 1000 * 60 * 15);
        org.springframework.amqp.core.Queue queue = new org.springframework.amqp.core.Queue(MQConstants.ORDER_RESULT_SUCCESS_DELAY_QUEUE, true, false, false, arguments);
        return queue;
    }

    /**
     * 超时订单队列绑定
     * @param orderDelayQueue
     * @return
     */
    @Bean
    public Binding binding1a(org.springframework.amqp.core.Queue orderDelayQueue) {
        return BindingBuilder.bind(orderDelayQueue)
                .to(new TopicExchange(MQConstants.ORDER_RESULT_EXCHANGE))
                .with(MQConstants.ORDER_RESULT_SUCCESS_KEY);
    }


    /**
     * 订单超时处理
     * @param map
     * @param deliveryTag
     * @param channel
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(MQConstants.ORDER_TIMEOUT_QUEUE),
            exchange = @Exchange(name = MQConstants.DELAY_EXCHANGE, type = "topic"),
            key = MQConstants.ORDER_DELAY_KEY))
    public void handlerTimeOutOrder(@Payload Map<String, Object> map, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        try {
            String orderNo = (String) map.get("orderNo");
            orderInfoService.cancelOrderDueTimeOut(orderNo);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            channel.basicAck(deliveryTag, false);
        }
    }


    /**
     * 取消本地标识消息处理
     * @param goodId
     * @param deliveryTag
     * @param channel
     * @throws IOException
     */
    @RabbitListener(bindings = {@QueueBinding(
            value = @Queue,
            exchange = @Exchange(value = MQConstants.SECKILL_OVER_SIGN_PUBSUB_EX, type = "fanout"))})
    public void handlerCancelSeckillOverSign(@Payload Long goodId, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        try {
            SeckillOrderController.map.put(goodId, false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            channel.basicAck(deliveryTag, false);
        }
    }
}