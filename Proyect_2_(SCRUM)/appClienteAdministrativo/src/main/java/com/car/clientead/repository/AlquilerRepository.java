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

import com.car.clientead.client.dto.AlquilerDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class AlquilerRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/alquileres";

    public AlquilerRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<AlquilerDto> findAll() {
        try {
            ResponseEntity<List<AlquilerDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<AlquilerDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de alquileres.", ex);
        }
    }

    public AlquilerDto findById(String id) {
        try {
            ResponseEntity<AlquilerDto> response = restTemplate.getForEntity(
                    baseUrl + "/" + id,
                    AlquilerDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el alquiler con ID: " + id, ex);
        }
    }

    public AlquilerDto create(AlquilerDto dto) {
        try {
            ResponseEntity<AlquilerDto> response = restTemplate.postForEntity(
                    baseUrl,
                    dto,
                    AlquilerDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo registrar el alquiler.", ex);
        }
    }

    public AlquilerDto update(String id, AlquilerDto dto) {
        try {
            HttpEntity<AlquilerDto> request = new HttpEntity<>(dto);
            ResponseEntity<AlquilerDto> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    request,
                    AlquilerDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar el alquiler con ID: " + id, ex);
        }
    }

    public void delete(String id) {
        try {
            restTemplate.delete(baseUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar el alquiler con ID: " + id, ex);
        }
    }

    public void marcarEntrega(String id) {
        try {
            restTemplate.postForEntity(baseUrl + "/" + id + "/entrega", null, Void.class);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo registrar la entrega del alquiler con ID: " + id, ex);
        }
    }
}
