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

import com.car.clientead.client.dto.VehiculoDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class VehiculoRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/vehiculos";

    public VehiculoRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<VehiculoDto> findAll() {
        try {
            ResponseEntity<List<VehiculoDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<VehiculoDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de vehículos.", ex);
        }
    }

    public VehiculoDto findById(String id) {
        try {
            ResponseEntity<VehiculoDto> response = restTemplate.getForEntity(
                    baseUrl + "/" + id,
                    VehiculoDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el vehículo con ID: " + id, ex);
        }
    }

    public VehiculoDto create(VehiculoDto dto) {
        try {
            ResponseEntity<VehiculoDto> response = restTemplate.postForEntity(
                    baseUrl,
                    dto,
                    VehiculoDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo crear el vehículo.", ex);
        }
    }

    public VehiculoDto update(String id, VehiculoDto dto) {
        try {
            HttpEntity<VehiculoDto> requestEntity = new HttpEntity<>(dto);
            ResponseEntity<VehiculoDto> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    requestEntity,
                    VehiculoDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar el vehículo con ID: " + id, ex);
        }
    }

    public void delete(String id) {
        try {
            restTemplate.delete(baseUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar el vehículo con ID: " + id, ex);
        }
    }
}
