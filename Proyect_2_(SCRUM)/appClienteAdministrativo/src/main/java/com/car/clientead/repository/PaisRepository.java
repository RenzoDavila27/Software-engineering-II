package com.car.clientead.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.car.clientead.client.dto.PaisDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class PaisRepository {
    
    private final RestTemplate restTemplate;
    private final String paisesUrl = "http://localhost:8080/api/paises";

    public PaisRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PaisDto> findAll() {
        try {
            ResponseEntity<List<PaisDto>> response = restTemplate.exchange(
                    paisesUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PaisDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de paises.", ex);
        }
    }
}
