package com.fioritech.car.bussiness.repository;

import com.fioritech.car.bussiness.dto.JwtResponse;
import com.fioritech.car.bussiness.dto.OAuthLoginRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Repository
public class AuthRepository {

    private final WebClient.Builder webClientBuilder;

    @Value("${app.server.base-url:http://localhost:8080}")
    private String appServerBaseUrl;

    public AuthRepository(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<JwtResponse> oauthLogin(OAuthLoginRequest request) {
        return webClientBuilder.baseUrl(appServerBaseUrl).build()
                .post()
                .uri("/seguridad/auth/oauth-login")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(JwtResponse.class);
    }
}
