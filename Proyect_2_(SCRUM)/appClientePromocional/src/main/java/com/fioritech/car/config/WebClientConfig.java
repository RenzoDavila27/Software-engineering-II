package com.fioritech.car.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        String apiBaseUrl = "http://localhost:8080";
        return builder
                .baseUrl(apiBaseUrl)
                .build();
    }

}
