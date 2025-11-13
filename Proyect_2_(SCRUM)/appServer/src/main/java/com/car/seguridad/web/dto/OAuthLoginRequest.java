package com.car.seguridad.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OAuthLoginRequest {

    @NotBlank
    private String username;
}
