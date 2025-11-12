package com.car.clientead.client.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

@ConfigurationProperties(prefix = "cliente.backend")
public class RemoteAuthProperties {

    /**
     * URL base del backend (por ejemplo http://localhost:8080).
     */
    private String baseUrl;

    /**
     * Path del módulo de autenticación (por defecto /seguridad/auth).
     */
    private String authPath = "/seguridad/auth";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAuthPath() {
        return authPath;
    }

    public void setAuthPath(String authPath) {
        if (StringUtils.hasText(authPath)) {
            this.authPath = authPath;
        }
    }
}
