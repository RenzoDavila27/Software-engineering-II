package com.example.demo.controller.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre
) {}
