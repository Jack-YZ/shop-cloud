package com.jz.alibabaprovider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AlibabaProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlibabaProviderApplication.class, args);
	}

}
