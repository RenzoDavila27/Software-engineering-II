package com.fioritech.car.bussiness.repository;

import com.fioritech.car.bussiness.dto.VehiculoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Repository
public class VehiculoRepository {

    @Autowired
    private final WebClient webClient;

    public VehiculoRepository(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<VehiculoDto> findAll() {
        return this.webClient.get().uri("/api/vehiculos").retrieve().bodyToFlux(VehiculoDto.class);
    }

}
