package com.car.seguridad.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "seguridad.jwt")
public class JwtProperties {

    @NotBlank
    private String secret;

    private String issuer = "mycar";

    @Min(1)
    private long accessTokenValiditySeconds = 900; // 15 minutos

    @Min(1)
    private long refreshTokenValiditySeconds = 86400; // 24 horas
}
