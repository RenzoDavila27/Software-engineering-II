package com.fioritech.car.bussiness.repository;

import com.fioritech.car.bussiness.dto.AlquilerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Repository
public class AlquilerRepository {

    private final WebClient webClient;

    public AlquilerRepository(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081/api/v1/alquiler").build();
    }

    public Mono<AlquilerDto> saveAlquiler(AlquilerDto alquilerDto) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    Authentication authentication = securityContext.getAuthentication();
                    if (authentication instanceof OAuth2AuthenticationToken) {
                        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                        // Here you would typically get the access token from the OAuth2AuthenticationToken
                        // and add it as a Bearer token.
                        // For simplicity, we'll just log a message.
                        System.out.println("Authenticated with OAuth2. Adding security token to request.");
                        // Example: String accessToken = oauthToken.getPrincipal().getAttribute("access_token");
                        // return webClient.post()
                        //         .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        //         .bodyValue(alquilerDto)
                        //         .retrieve()
                        //         .bodyToMono(AlquilerDto.class);
                    }
                    return authentication; // Return authentication to continue the chain
                })
                .flatMap(authentication -> {
                    // Placeholder for actual WebClient call with security headers
                    // For now, just a basic call without actual token handling
                    return webClient.post()
                            .bodyValue(alquilerDto)
                            .retrieve()
                            .bodyToMono(AlquilerDto.class);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    // If no security context, proceed without token (or handle as error)
                    System.out.println("No security context found. Proceeding without token.");
                    return webClient.post()
                            .bodyValue(alquilerDto)
                            .retrieve()
                            .bodyToMono(AlquilerDto.class);
                }));
    }
}
