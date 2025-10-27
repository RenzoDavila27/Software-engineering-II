package com.example.apiexterna.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.example.apiexterna.models.Imdb;
import com.example.apiexterna.models.MovieDetailsDTO;
import java.util.List;

import org.springframework.beans.factory.annotation.Value; 

@Service
public class ImdbService {

    private final WebClient webClient;

    @Value("${imdb.api.key}")
    private String apiKey;

    public ImdbService( final WebClient webClient) {
        this.webClient = webClient;
    
    }

    public Mono<String> obtenerRatingPorId(String id) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api/imdb/{id}/rating")
                .build(id))
            .retrieve()
            .bodyToMono(String.class);
    }

    public Mono<List<MovieDetailsDTO>> obtenerListaProximosEstrenos() {
    return webClient.get()
        .uri("/api/imdb/top-box-office") // Nuevo endpoint de lista detallada
        .retrieve()
        .bodyToFlux(MovieDetailsDTO.class) // Usamos bodyToFlux para mapear un array de objetos
        .collectList(); // Lo convertimos en un Mono<List<MovieDetailsDto>>
    }
}