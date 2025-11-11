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

import com.car.clientead.client.dto.DepartamentoDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class DepartamentoRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/departamentos";

    public DepartamentoRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<DepartamentoDto> findAll() {
        try {
            ResponseEntity<List<DepartamentoDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<DepartamentoDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de departamentos.", ex);
        }
    }

    public DepartamentoDto findById(String id) {
        try {
            ResponseEntity<DepartamentoDto> response = restTemplate.getForEntity(
                    baseUrl + "/" + id,
                    DepartamentoDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el departamento con ID: " + id, ex);
        }
    }

    public DepartamentoDto create(DepartamentoDto dto) {
        try {
            ResponseEntity<DepartamentoDto> response = restTemplate.postForEntity(
                    baseUrl,
                    dto,
                    DepartamentoDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo crear el departamento.", ex);
        }
    }

    public DepartamentoDto update(String id, DepartamentoDto dto) {
        try {
            HttpEntity<DepartamentoDto> requestEntity = new HttpEntity<>(dto);
            ResponseEntity<DepartamentoDto> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    requestEntity,
                    DepartamentoDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar el departamento con ID: " + id, ex);
        }
    }

    public void delete(String id) {
        try {
            restTemplate.delete(baseUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar el departamento con ID: " + id, ex);
        }
    }
}
