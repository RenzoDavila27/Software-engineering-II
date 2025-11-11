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

import com.car.clientead.client.dto.ProvinciaDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class ProvinciaRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/provincias";

    public ProvinciaRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ProvinciaDto> findAll() {
        try {
            ResponseEntity<List<ProvinciaDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ProvinciaDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de provincias.", ex);
        }
    }

    public ProvinciaDto findById(String id) {
        try {
            ResponseEntity<ProvinciaDto> response = restTemplate.getForEntity(
                    baseUrl + "/" + id,
                    ProvinciaDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener la provincia con ID: " + id, ex);
        }
    }

    public ProvinciaDto create(ProvinciaDto dto) {
        try {
            ResponseEntity<ProvinciaDto> response = restTemplate.postForEntity(
                    baseUrl,
                    dto,
                    ProvinciaDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo crear la provincia.", ex);
        }
    }

    public ProvinciaDto update(String id, ProvinciaDto dto) {
        try {
            HttpEntity<ProvinciaDto> requestEntity = new HttpEntity<>(dto);
            ResponseEntity<ProvinciaDto> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    requestEntity,
                    ProvinciaDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar la provincia con ID: " + id, ex);
        }
    }

    public void delete(String id) {
        try {
            restTemplate.delete(baseUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar la provincia con ID: " + id, ex);
        }
    }
}
