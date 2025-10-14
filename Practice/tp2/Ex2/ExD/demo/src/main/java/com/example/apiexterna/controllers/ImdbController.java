package com.example.apiexterna.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.apiexterna.models.MovieDetailsDTO;
import java.util.List;

import com.example.apiexterna.services.ImdbService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/imdb")
public class ImdbController {
    
    private final ImdbService imdbService;

    public ImdbController(ImdbService imdbService) {
        this.imdbService = imdbService;
    }

    @GetMapping("/rating/{id}") 
    public Mono<String> obtenerRatingPorId(@PathVariable String id) {
        return imdbService.obtenerRatingPorId(id);
    }

    @GetMapping("/proximos-estrenos")
    public Mono<List<MovieDetailsDTO>> getProximosEstrenos() {
        // URL que el frontend llamará: /api/imdb/proximos-estrenos
        return imdbService.obtenerListaProximosEstrenos();
    }
}