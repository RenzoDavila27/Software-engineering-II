package com.car.clientead.client.config;

import com.car.clientead.client.auth.RemoteAuthProperties;
import com.car.clientead.security.JwtAuthorizationInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(RemoteAuthProperties.class)
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder,
                                     JwtAuthorizationInterceptor interceptor) {
        return builder
                .additionalInterceptors(interceptor)
                .build();
    }
}
