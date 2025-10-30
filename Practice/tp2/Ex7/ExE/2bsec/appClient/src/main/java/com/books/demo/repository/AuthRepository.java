package com.books.demo.repository;

import com.books.demo.client.dto.JwtResponse;
import com.books.demo.client.dto.LoginRequest;
import com.books.demo.client.dto.MessageResponse;
import com.books.demo.client.dto.RegisterRequest;
import com.books.demo.client.exception.ApiClientException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Repository
public class AuthRepository {

    private static final String AUTH_BASE_URL = "http://localhost:8080/api/auth";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final RestTemplate restTemplate;

    public AuthRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public JwtResponse login(LoginRequest loginRequest) {
        try {
            return restTemplate.postForObject(AUTH_BASE_URL + "/login", loginRequest, JwtResponse.class);
        } catch (HttpClientErrorException ex) {
            throw new ApiClientException(resolverMensajeError(ex), ex);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo iniciar sesión en este momento.", ex);
        }
    }

    public MessageResponse register(RegisterRequest registerRequest) {
        try {
            ResponseEntity<MessageResponse> response = restTemplate.exchange(
                    AUTH_BASE_URL + "/register",
                    HttpMethod.POST,
                    new HttpEntity<>(registerRequest),
                    MessageResponse.class);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            throw new ApiClientException(resolverMensajeError(ex), ex);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo completar el registro en este momento.", ex);
        }
    }

    private String resolverMensajeError(HttpClientErrorException ex) {
        String responseBody = ex.getResponseBodyAsString();
        if (responseBody != null && !responseBody.isBlank()) {
            try {
                JsonNode node = OBJECT_MAPPER.readTree(responseBody);
                if (node.hasNonNull("message") && !node.get("message").asText().isBlank()) {
                    return node.get("message").asText();
                }
                if (node.hasNonNull("error") && !node.get("error").asText().isBlank()) {
                    String errorValue = node.get("error").asText();
                    if ("Unauthorized".equalsIgnoreCase(errorValue)) {
                        return "Credenciales inválidas.";
                    }
                    return errorValue;
                }
            } catch (Exception ignored) {
                // Ignored; fallback to raw response
            }
            return "No fue posible iniciar sesión. Verifique los datos ingresados.";
        }
        int status = ex.getStatusCode().value();
        return switch (status) {
            case 401 -> "Credenciales inválidas.";
            case 400 -> "La solicitud enviada no es válida.";
            case 409 -> "Ya existe un registro con los datos enviados.";
            default -> "Error al comunicarse con el servicio de autenticación.";
        };
    }
}
