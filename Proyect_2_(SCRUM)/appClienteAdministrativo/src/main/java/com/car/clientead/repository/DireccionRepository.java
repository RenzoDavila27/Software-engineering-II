package com.car.clientead.repository;

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

    public DireccionDto findById(String id) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + id, DireccionDto.class);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener la dirección con ID: " + id, ex);
        }
    }
}
