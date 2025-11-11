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

    public PaisDto findById(String id) {
        try {
            ResponseEntity<PaisDto> response = restTemplate.getForEntity(
                    paisesUrl + "/" + id,
                    PaisDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el pais con ID: " + id, ex);
        }
    }

    public PaisDto create(PaisDto dto) {
        try {
            ResponseEntity<PaisDto> response = restTemplate.postForEntity(
                    paisesUrl,
                    dto,
                    PaisDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo crear el pais.", ex);
        }
    }

    public PaisDto update(String id, PaisDto dto) {
        try {
            HttpEntity<PaisDto> requestEntity = new HttpEntity<>(dto);
            ResponseEntity<PaisDto> response = restTemplate.exchange(
                    paisesUrl + "/" + id,
                    HttpMethod.PUT,
                    requestEntity,
                    PaisDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar el pais con ID: " + id, ex);
        }
    }

    public void delete(String id) {
        try {
            restTemplate.delete(paisesUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar el pais con ID: " + id, ex);
        }
    }
}
