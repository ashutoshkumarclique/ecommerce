package com.ecommerce.apigateway.config;

import com.ecommerce.apigateway.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    private static final String[] OPEN_API_ENDPOINTS = {
            "/api/auth/login",
            "/api/auth/signup"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // ✅ Skip public endpoints
        for (String endpoint : OPEN_API_ENDPOINTS) {
            if (path.startsWith(endpoint)) {
                return chain.filter(exchange);
            }
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // ✅ Extract claims and forward user details
        String userId = jwtUtil.extractUserId(token);
        String role = jwtUtil.extractRole(token);

        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(r -> r.headers(headers -> {
                    headers.add("x-user-id", userId);
                    headers.add("x-user-role", role);
                }))
                .build();

        return chain.filter(modifiedExchange);
    }

    @Override
    public int getOrder() {
        return -1; // High priority
    }
}
