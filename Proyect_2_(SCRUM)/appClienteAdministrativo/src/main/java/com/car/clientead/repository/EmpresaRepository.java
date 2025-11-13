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

import com.car.clientead.client.dto.EmpresaDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class EmpresaRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/empresas";

    public EmpresaRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<EmpresaDto> findAll() {
        try {
            ResponseEntity<List<EmpresaDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<EmpresaDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de empresas.", ex);
        }
    }

    public EmpresaDto findById(String id) {
        try {
            ResponseEntity<EmpresaDto> response = restTemplate.getForEntity(
                    baseUrl + "/" + id,
                    EmpresaDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener la empresa con ID: " + id, ex);
        }
    }

    public EmpresaDto create(EmpresaDto dto) {
        try {
            ResponseEntity<EmpresaDto> response = restTemplate.postForEntity(
                    baseUrl,
                    dto,
                    EmpresaDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo registrar la empresa.", ex);
        }
    }

    public EmpresaDto update(String id, EmpresaDto dto) {
        try {
            HttpEntity<EmpresaDto> request = new HttpEntity<>(dto);
            ResponseEntity<EmpresaDto> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    request,
                    EmpresaDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar la empresa con ID: " + id, ex);
        }
    }

    public void delete(String id) {
        try {
            restTemplate.delete(baseUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar la empresa con ID: " + id, ex);
        }
    }
}
