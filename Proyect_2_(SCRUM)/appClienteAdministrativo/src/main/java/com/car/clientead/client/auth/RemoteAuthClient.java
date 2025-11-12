package com.car.clientead.client.auth;

import com.car.clientead.client.auth.dto.JwtResponse;
import com.car.clientead.client.auth.dto.LoginRequest;
import java.net.URI;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class RemoteAuthClient {

    private final RestTemplate restTemplate;
    private final URI loginUri;

    public RemoteAuthClient(RestTemplate restTemplate, RemoteAuthProperties properties) {
        this.restTemplate = restTemplate;
        if (properties.getBaseUrl() == null) {
            throw new IllegalStateException("cliente.backend.base-url no está configurado");
        }
        this.loginUri = UriComponentsBuilder.fromHttpUrl(properties.getBaseUrl())
                .path(properties.getAuthPath())
                .path("/login")
                .build()
                .toUri();
    }

    public JwtResponse login(String username, String password) {
        LoginRequest request = new LoginRequest(username, password);
        try {
            ResponseEntity<JwtResponse> response = restTemplate.postForEntity(loginUri, request, JwtResponse.class);
            return Optional.ofNullable(response.getBody())
                    .orElseThrow(() -> new RemoteAuthenticationException("Respuesta vacía del servidor de autenticación"));
        } catch (HttpStatusCodeException ex) {
            throw new RemoteAuthenticationException("Credenciales inválidas", ex);
        } catch (RestClientException ex) {
            throw new RemoteAuthenticationException("Error comunicándose con el servidor de autenticación", ex);
        }
    }
}
