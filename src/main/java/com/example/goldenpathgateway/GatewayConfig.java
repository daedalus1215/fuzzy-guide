package com.example.goldenpathgateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p.path("/acc")
                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8080/accounts/541/transactions"))
                .build();
    }
}

