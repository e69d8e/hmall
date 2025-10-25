package com.hmall.api.config;

import com.hmall.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

// 不加@Configuration注解
public class DefaultFeignConfig {
    @Bean // feign日志级别
    public Logger.Level feignLoggerLevel(){
        return Logger.Level.BASIC;
    }
    @Bean // 微服务之间的用户信息传递
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                Long userId = UserContext.getUser();
                if (userId == null){
                    return;
                }
                requestTemplate.header("user-info", userId.toString());
            }
        };
    }
}
