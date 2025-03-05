package com.hmall.user;

import com.hmall.api.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.hmall.user.mapper")
@SpringBootApplication
// 启用Feign
// basePackages：指定需要扫描的包路径，Spring 会在这个包及其子包中查找带有 @FeignClient 注解的接口。
//defaultConfiguration：指定默认的 Feign 客户端配置类，这些配置将应用于所有 Feign 客户端，除非某个客户端显式指定了不同的配置。
@EnableFeignClients(basePackages = "com.hmall.api.client", defaultConfiguration = DefaultFeignConfig.class)
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}