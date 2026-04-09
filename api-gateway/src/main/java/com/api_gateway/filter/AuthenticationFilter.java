package com.api_gateway.filter;

import com.api_gateway.security.JwtUtil;
import com.api_gateway.security.RouteValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


//JWT authentication filter for API Gateway.
@Component
@Slf4j
@RequiredArgsConstructor
//GlobalFilter
//This filter runs for gateway requests
//Ordered
//Decides filter priority/order
public class AuthenticationFilter implements GlobalFilter, Ordered {

// routeValidator
// tells whether route is secured or public
    private final RouteValidator routeValidator;
// jwtUtil
// used to validate JWT and extract data from it
    private final JwtUtil jwtUtil;

    //exchange = current request + response
    //chain = next filter / next service
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Filter running");
        String path = exchange.getRequest().getURI().getPath();

        if (!routeValidator.isSecured.test(path)) {
            log.info("unsecured url");
            return chain.filter(exchange);
        }

        if (exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION) == null) {
            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = exchange.getRequest().getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return onError(exchange, "Invalid Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(7);
        log.info("Token found validating token" + token);

        if (!jwtUtil.isTokenValid(token)) {
            return onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
        }


        String username = jwtUtil.extractUsername(token);
        String role = jwtUtil.extractRole(token);
        Integer userId = jwtUtil.extractUserId(token);

        // Add headers to downstream services
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(builder -> builder
                        .header("X-User-Name", username)
                        .header("X-User-Role", role)
                        .header("X-User-Id", String.valueOf(userId))
                )
                .build();

        log.info("Token validation successful");
        return chain.filter(modifiedExchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus status) {
        log.info("Invalid token");
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }
//This filter should run early
//Lower number = higher priority
    @Override
    public int getOrder() {
        return -1;
    }
}