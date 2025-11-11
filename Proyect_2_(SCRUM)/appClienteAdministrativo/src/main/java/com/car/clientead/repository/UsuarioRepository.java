package com.car.clientead.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.car.clientead.client.dto.UsuarioDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class UsuarioRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/usuarios";

    public UsuarioRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<UsuarioDto> findAll() {
        try {
            ResponseEntity<List<UsuarioDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<UsuarioDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de usuarios.", ex);
        }
    }

    public UsuarioDto findById(String id) {
        try {
            ResponseEntity<UsuarioDto> response = restTemplate.getForEntity(
                    baseUrl + "/" + id,
                    UsuarioDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el usuario con ID: " + id, ex);
        }
    }

    public UsuarioDto create(UsuarioDto dto) {
        try {
            ResponseEntity<UsuarioDto> response = restTemplate.postForEntity(
                    baseUrl,
                    dto,
                    UsuarioDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo crear el usuario.", ex);
        }
    }

    public UsuarioDto update(String id, UsuarioDto dto) {
        try {
            HttpEntity<UsuarioDto> request = new HttpEntity<>(dto);
            ResponseEntity<UsuarioDto> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    request,
                    UsuarioDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar el usuario con ID: " + id, ex);
        }
    }

    public void delete(String id) {
        try {
            restTemplate.delete(baseUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar el usuario con ID: " + id, ex);
        }
    }
}
