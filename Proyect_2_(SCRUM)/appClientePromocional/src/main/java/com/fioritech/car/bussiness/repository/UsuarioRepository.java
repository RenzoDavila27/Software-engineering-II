package com.fioritech.car.bussiness.repository;

import com.fioritech.car.bussiness.dto.RegistrationForm;
import com.fioritech.car.bussiness.dto.UsuarioApiDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Repository
public class UsuarioRepository {

    private final WebClient webClient;

    @Autowired
    public UsuarioRepository(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> loginUserApi(String email) {
        return webClient.post()
                .uri("/api/usuarios/login")
                .bodyValue(email)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<Void> registerUserApi(UsuarioApiDto apiDto) {

        return webClient.post()
                .uri("/api/usuarios/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(apiDto)
                .retrieve()
                .bodyToMono(Void.class);
    }
}