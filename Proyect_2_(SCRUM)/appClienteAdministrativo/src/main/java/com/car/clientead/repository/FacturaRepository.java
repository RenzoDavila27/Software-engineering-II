package com.car.clientead.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.car.clientead.client.dto.FacturaDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class FacturaRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/facturas";

    public FacturaRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<FacturaDto> findAll() {
        try {
            ResponseEntity<List<FacturaDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<FacturaDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de facturas.", ex);
        }
    }

    public FacturaDto findById(String id) {
        try {
            ResponseEntity<FacturaDto> response = restTemplate.getForEntity(
                    baseUrl + "/" + id,
                    FacturaDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener la factura con ID: " + id, ex);
        }
    }
}
