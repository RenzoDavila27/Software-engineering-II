package com.example.demo.controller.api.dto;

public record VideojuegoResponse(
        Long id,
        String titulo,
        String rutaimg,
        float precio,
        Short cantidad,
        String descripcion,
        Boolean oferta,
        String fechalanzamiento,
        CategoriaResponse categoria,
        EstudioResponse estudio
) {}
