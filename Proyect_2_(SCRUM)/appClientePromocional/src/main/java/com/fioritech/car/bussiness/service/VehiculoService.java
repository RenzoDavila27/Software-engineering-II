package com.fioritech.car.bussiness.service;

import com.fioritech.car.bussiness.dto.VehiculoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final WebClient.Builder webClientBuilder;

    public Flux<VehiculoDto> findAll() {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/vehiculos")
                .retrieve()
                .bodyToFlux(VehiculoDto.class);
    }

    public Mono<VehiculoDto> findById(String id) {
        return findAll().filter(v -> v.getId().equals(id)).next();
    }

    private VehiculoDto validate(VehiculoDto vehiculoDto) {
        // TODO: Add validation logic
        return vehiculoDto;
    }
}
