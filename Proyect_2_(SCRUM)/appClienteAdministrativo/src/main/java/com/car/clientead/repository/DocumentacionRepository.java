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

import com.car.clientead.client.dto.DocumentacionDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class DocumentacionRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/documentaciones";

    public DocumentacionRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<DocumentacionDto> findAll() {
        try {
            ResponseEntity<List<DocumentacionDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<DocumentacionDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener la documentación registrada.", ex);
        }
    }

    public DocumentacionDto findById(String id) {
        try {
            ResponseEntity<DocumentacionDto> response = restTemplate.getForEntity(
                    baseUrl + "/" + id,
                    DocumentacionDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener la documentación con ID: " + id, ex);
        }
    }

    public DocumentacionDto create(DocumentacionDto dto) {
        try {
            ResponseEntity<DocumentacionDto> response = restTemplate.postForEntity(
                    baseUrl,
                    dto,
                    DocumentacionDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo registrar la documentación.", ex);
        }
    }

    public DocumentacionDto update(String id, DocumentacionDto dto) {
        try {
            HttpEntity<DocumentacionDto> request = new HttpEntity<>(dto);
            ResponseEntity<DocumentacionDto> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    request,
                    DocumentacionDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar la documentación con ID: " + id, ex);
        }
    }

    public void delete(String id) {
        try {
            restTemplate.delete(baseUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar la documentación con ID: " + id, ex);
        }
    }
}
