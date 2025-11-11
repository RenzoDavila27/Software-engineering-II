package com.car.clientead.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.car.clientead.client.dto.NacionalidadDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class NacionalidadRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/nacionalidades";

    public NacionalidadRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<NacionalidadDto> findAll() {
        try {
            ResponseEntity<List<NacionalidadDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<NacionalidadDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de nacionalidades.", ex);
        }
    }
}
