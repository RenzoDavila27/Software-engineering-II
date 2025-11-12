package com.car.clientead.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.car.clientead.client.dto.DireccionDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class DireccionRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/direcciones";

    public DireccionRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<DireccionDto> findAll() {
        try {
            ResponseEntity<List<DireccionDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<DireccionDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de direcciones.", ex);
        }
    }

    public DireccionDto findById(String id) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + id, DireccionDto.class);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener la dirección con ID: " + id, ex);
        }
    }
}
