package com.hmall.gateway.filters;

import com.hmall.gateway.config.AuthProperties;
import com.hmall.gateway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    private final JwtTool jwtTool;
    private final AuthProperties authProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();  // 路径匹配器
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取request
        ServerHttpRequest request = exchange.getRequest();
        // 判断是否需要认证
        for (String excludePath : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(excludePath, request.getURI().getPath())) {
                return chain.filter(exchange);
            }
        }
        // 获取请求头
        HttpHeaders headers = request.getHeaders();
        if (headers.containsKey("authorization")) {
            // 获取token
            String token = headers.getFirst("authorization");
            Long userId = null;
            // 校验
            try {
                userId = jwtTool.parseToken(token);
                System.out.println("userId:" + userId);
            } catch (Exception e) {
                // 将响应状态码设置为401
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                // 拦截
                return exchange.getResponse().setComplete();
            }
//            // 将 userId 添加到请求头
//            ServerHttpRequest newRequest = request.mutate().header("authorization", userId.toString()).build();
//            // 替换request
//            ServerWebExchange serverWebExchange = exchange.mutate().request(newRequest).build();
            // 传递用户信息给微服务
            String userInfo = userId.toString();
            ServerWebExchange serverWebExchange = exchange.mutate().request(builder -> builder.header("user-info", userInfo)).build();
            return chain.filter(serverWebExchange); // 放行
        }
        // 将响应状态码设置为401
        exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        // 拦截
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
