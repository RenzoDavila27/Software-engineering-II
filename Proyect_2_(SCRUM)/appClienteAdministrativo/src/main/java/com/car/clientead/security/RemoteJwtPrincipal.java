package com.car.clientead.security;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class RemoteJwtPrincipal implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String username;
    private final String accessToken;
    private final String refreshToken;
    private final Instant expiresAt;
    private final List<String> roles;

    public RemoteJwtPrincipal(String username,
                              String accessToken,
                              String refreshToken,
                              Instant expiresAt,
                              List<String> roles) {
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.roles = roles != null ? List.copyOf(roles) : Collections.emptyList();
    }

    public String getUsername() {
        return username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public List<String> getRoles() {
        return roles;
    }
}
