package com.fioritech.car.repository;

import com.fioritech.car.dto.VehiculoDto;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Repository
public class VehiculoRepository {

    private final WebClient webClient;

    public VehiculoRepository(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    public Flux<VehiculoDto> findAll() {
        return this.webClient.get().uri("/vehiculos").retrieve().bodyToFlux(VehiculoDto.class);
    }
}
