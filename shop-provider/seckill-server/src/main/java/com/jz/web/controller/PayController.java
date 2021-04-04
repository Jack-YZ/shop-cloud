package com.jz.web.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.jz.alipay.AlipayConfigProperties;
import com.jz.domain.OrderInfo;
import com.jz.service.IOrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Map;

@RestController
public class PayController {

    @Autowired
    private IOrderInfoService orderInfoService;
    @Autowired
    private AlipayConfigProperties alipayConfigProperties;
    @Autowired
    private AlipayClient alipayClient;


    /**
     * 支付功能
     * @param orderNo
     * @param response
     */
    @RequestMapping("/alipay")
    public void payOrder(String orderNo, HttpServletResponse response){
        //支付宝支付，项目中可能有订单支付，会员支付，支付其他东西.
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(alipayConfigProperties.returnUrl);
        alipayRequest.setNotifyUrl(alipayConfigProperties.notifyUrl);

        //获取信息
        OrderInfo orderInfo = orderInfoService.find(orderNo);
        if(orderInfo==null){
            try {
                response.sendRedirect("http://localhost:/50x.html");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        //商户订单号，商户网站订单系统中唯一订单号，必填  ==>orderNo
        //付款金额，必填
        BigDecimal total_amount = orderInfo.getSeckillPrice();
        //订单名称，必填
        String subject = "订单名称" + orderInfo.getGoodName();
        //商品描述，可空
        String body = "商品详情" + orderInfo.getGoodName();
        //把前台的参数封装成JSON字符串
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ orderNo +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"body\":\""+ body +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        try {
            //请求
            String result = alipayClient.pageExecute(alipayRequest).getBody();
            //输出
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 异步通知页面,先于同步通知执行
     * @param params
     * @return
     */
    @RequestMapping("/notify_url")
    public void notifyUrl(@RequestParam Map<String,String> params,HttpServletResponse response) throws IOException {
        System.out.println("异步通知");
        PrintWriter out = response.getWriter();
        try {
            //获取支付宝POST过来反馈信息params
            //商户订单号
            String out_trade_no = new String(params.get("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //支付宝交易号--可以在订单类后面添加支付宝交易号属性
            String trade_no = new String(params.get("trade_no").getBytes("ISO-8859-1"),"UTF-8");
            //交易状态
            String trade_status = new String(params.get("trade_status").getBytes("ISO-8859-1"),"UTF-8");

            boolean signVerified = AlipaySignature.rsaCheckV1(params,
                    alipayConfigProperties.getAlipayPublicKey(),
                    alipayConfigProperties.getCharset(),
                    alipayConfigProperties.getSignType());//调用SDK验证签名
            System.out.println("验证签名是否成功:"+signVerified);
            if(signVerified) {//验证成功
                if(trade_status.equals("TRADE_FINISHED")){
                    //表示订单之前已经处理过了
                    out.println("success");
                }else if (trade_status.equals("TRADE_SUCCESS")){
                    //表示订单交易成功
                    //注意:在异步回调的时候，业务做了，但是在返回"success"的时候，支付宝没有收到响应请求.继续在发送请求.此方法有可能会被多次调用
                    //做幂等性判断，会使用支付流水表来控制，防止业务重复执行.
                    OrderInfo orderInfo = orderInfoService.find(out_trade_no);
                    if (orderInfo.getStatus().equals(OrderInfo.STATUS_ARREARAGE)){
                        //未付款,修改订单状态
                        System.out.println("修改订单成功");
                        int count  = orderInfoService.updataPayStatus(out_trade_no,OrderInfo.STATUS_ACCOUNT_PAID);
                        if (count > 0){
                            out.println("success");
                        }else {
                            //在支付修改订单状态的时候，刚好已经超时取消订单了，订单状态是不能修改的。进行退款流程.
                            //往MQ中发送消息，做退款操作，人工需要审核之后才进行退款操作.
                        }
                    }else if (orderInfo.getStatus().equals(OrderInfo.STATUS_ACCOUNT_PAID)){
                        //已付款,直接返回
                        out.println("success");
                    }else{
                        //超时等特殊情况,但是已经支付,需要人工处理,进行退款的操作
                    }
                }
            }else{
                out.println("fail");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            out.println("fail");
        }
    }



    /**
     * 同步通知页面
     * @param params
     * @param response
     */
    @RequestMapping("/return_url")
    public void returnUrl(@RequestParam Map<String,String> params,HttpServletResponse response){
        System.out.println("同步通知");
        try {
            boolean signVerified = AlipaySignature.rsaCheckV1(params,
                    alipayConfigProperties.getAlipayPublicKey(),
                    alipayConfigProperties.getCharset(),
                    alipayConfigProperties.getSignType());//调用SDK验证签名
            System.out.println("验证签名是否成功:"+signVerified);
            if(signVerified) {//验证成功
                //商户订单号
                String orderNo = params.get("out_trade_no");
                response.sendRedirect("http://localhost/order_detail.html?orderNo="+orderNo);
            }else {
                response.sendRedirect("http://localhost:/50x.html");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
