package com.car.controller.rest.api.dto;

import jakarta.validation.constraints.NotBlank;

public record DocumentoAdjuntoDto(
    @NotBlank String nombreArchivo,
    @NotBlank String contentType,
    @NotBlank String contenidoBase64
) {
}
