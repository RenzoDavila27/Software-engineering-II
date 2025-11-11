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

import com.car.clientead.client.dto.LocalidadDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class LocalidadRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/localidades";

    public LocalidadRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<LocalidadDto> findAll() {
        try {
            ResponseEntity<List<LocalidadDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<LocalidadDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de localidades.", ex);
        }
    }

    public LocalidadDto findById(String id) {
        try {
            ResponseEntity<LocalidadDto> response = restTemplate.getForEntity(
                    baseUrl + "/" + id,
                    LocalidadDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener la localidad con ID: " + id, ex);
        }
    }

    public LocalidadDto create(LocalidadDto dto) {
        try {
            ResponseEntity<LocalidadDto> response = restTemplate.postForEntity(
                    baseUrl,
                    dto,
                    LocalidadDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo crear la localidad.", ex);
        }
    }

    public LocalidadDto update(String id, LocalidadDto dto) {
        try {
            HttpEntity<LocalidadDto> requestEntity = new HttpEntity<>(dto);
            ResponseEntity<LocalidadDto> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    requestEntity,
                    LocalidadDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar la localidad con ID: " + id, ex);
        }
    }

    public void delete(String id) {
        try {
            restTemplate.delete(baseUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar la localidad con ID: " + id, ex);
        }
    }
}
