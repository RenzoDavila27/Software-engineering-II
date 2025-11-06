package com.example.demo.controller.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VideojuegoRequest(
        @NotBlank(message = "El título es obligatorio")
        String titulo,
        @NotBlank(message = "Debe indicar la ruta de la imagen")
        String rutaimg,
        @Positive(message = "El precio debe ser mayor a cero")
        float precio,
        @NotNull(message = "Debe indicar la cantidad")
        Short cantidad,
        @NotBlank(message = "Debe indicar la descripción")
        String descripcion,
        Boolean oferta,
        @NotBlank(message = "Debe indicar la fecha de lanzamiento")
        String fechalanzamiento,
        @NotNull(message = "Debe indicar la categoría")
        Long categoriaId,
        @NotNull(message = "Debe indicar el estudio")
        Long estudioId
) {}
