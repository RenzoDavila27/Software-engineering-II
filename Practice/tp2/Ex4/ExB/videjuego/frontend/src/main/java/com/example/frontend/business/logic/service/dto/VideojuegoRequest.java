package com.example.frontend.business.logic.service.dto;

public record VideojuegoRequest(
        String titulo,
        String rutaimg,
        float precio,
        Short cantidad,
        String descripcion,
        Boolean oferta,
        String fechalanzamiento,
        Long categoriaId,
        Long estudioId
) {}
