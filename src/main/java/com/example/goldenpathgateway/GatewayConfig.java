package com.example.goldenpathgateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public InAndOutFilter inAndOut() {
        return new InAndOutFilter();
    }
}
