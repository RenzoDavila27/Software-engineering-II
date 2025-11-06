package com.example.demo.controller.api.dto;

import jakarta.validation.constraints.NotBlank;

public record EstudioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre
) {}
