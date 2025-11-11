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

import com.car.clientead.client.dto.CostoVehiculoDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class CostoVehiculoRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/costos-vehiculo";

    public CostoVehiculoRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<CostoVehiculoDto> findAll() {
        try {
            ResponseEntity<List<CostoVehiculoDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CostoVehiculoDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de costos de vehículo.", ex);
        }
    }

    public List<CostoVehiculoDto> findByCaracteristica(String caracteristicaId) {
        try {
            ResponseEntity<List<CostoVehiculoDto>> response = restTemplate.exchange(
                    baseUrl + "/listar/" + caracteristicaId,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CostoVehiculoDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener los costos del vehículo con característica ID: " + caracteristicaId, ex);
        }
    }

    public CostoVehiculoDto findById(String id) {
        try {
            ResponseEntity<CostoVehiculoDto> response = restTemplate.getForEntity(
                    baseUrl + "/" + id,
                    CostoVehiculoDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el costo de vehículo con ID: " + id, ex);
        }
    }

    public CostoVehiculoDto create(CostoVehiculoDto dto) {
        try {
            ResponseEntity<CostoVehiculoDto> response = restTemplate.postForEntity(
                    baseUrl,
                    dto,
                    CostoVehiculoDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo crear el costo de vehículo.", ex);
        }
    }

    public CostoVehiculoDto update(String id, CostoVehiculoDto dto) {
        try {
            HttpEntity<CostoVehiculoDto> requestEntity = new HttpEntity<>(dto);
            ResponseEntity<CostoVehiculoDto> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    requestEntity,
                    CostoVehiculoDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar el costo de vehículo con ID: " + id, ex);
        }
    }

    public void delete(String id) {
        try {
            restTemplate.delete(baseUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar el costo de vehículo con ID: " + id, ex);
        }
    }
}
