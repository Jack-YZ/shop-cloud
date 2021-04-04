package com.jz.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayClientConfig {

    @Autowired
    private AlipayConfigProperties alipayConfigProperties;

    @Bean
    public AlipayClient alipayClient(){
        AlipayClient alipayClient = new DefaultAlipayClient(
                alipayConfigProperties.getGatewayUrl(),
                alipayConfigProperties.getAppId(),
                alipayConfigProperties.getRsaPrivateKey(),
                alipayConfigProperties.getFormat(),
                alipayConfigProperties.getCharset(),
                alipayConfigProperties.getAlipayPublicKey(),
                alipayConfigProperties.getSignType());
        return alipayClient;
    }
}
