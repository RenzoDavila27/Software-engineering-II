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

import com.car.clientead.client.dto.PromocionDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class PromocionRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/promociones";

    public PromocionRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PromocionDto> findAll() {
        try {
            ResponseEntity<List<PromocionDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PromocionDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de promociones.", ex);
        }
    }

    public PromocionDto findById(String id) {
        try {
            ResponseEntity<PromocionDto> response = restTemplate.getForEntity(
                    baseUrl + "/" + id,
                    PromocionDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener la promoción con ID: " + id, ex);
        }
    }

    public PromocionDto create(PromocionDto dto) {
        try {
            ResponseEntity<PromocionDto> response = restTemplate.postForEntity(
                    baseUrl,
                    dto,
                    PromocionDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo crear la promoción.", ex);
        }
    }

    public PromocionDto update(String id, PromocionDto dto) {
        try {
            HttpEntity<PromocionDto> request = new HttpEntity<>(dto);
            ResponseEntity<PromocionDto> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    request,
                    PromocionDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar la promoción con ID: " + id, ex);
        }
    }

    public void delete(String id) {
        try {
            restTemplate.delete(baseUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar la promoción con ID: " + id, ex);
        }
    }
}
