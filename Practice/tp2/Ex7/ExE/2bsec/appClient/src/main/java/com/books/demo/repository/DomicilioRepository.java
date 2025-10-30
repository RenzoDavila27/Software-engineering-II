package com.books.demo.repository;

import com.books.demo.client.dto.DomicilioDto;
import com.books.demo.client.exception.ApiClientException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Repository
public class DomicilioRepository {

    private final RestTemplate restTemplate;
    private final String domiciliosUrl = "http://localhost:8080/api/domicilios";

    public DomicilioRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<DomicilioDto> findAll() {
        try {
            ResponseEntity<List<DomicilioDto>> response = restTemplate.exchange(
                    domiciliosUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<DomicilioDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de domicilios.", ex);
        }
    }

    public Optional<DomicilioDto> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            DomicilioDto dto = restTemplate.getForObject(domiciliosUrl + "/" + id, DomicilioDto.class);
            return Optional.ofNullable(dto);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new ApiClientException("Error al consultar el domicilio con id " + id + ".", ex);
        }
    }

    public DomicilioDto save(DomicilioDto domicilio) {
        try {
            return restTemplate.postForObject(domiciliosUrl, domicilio, DomicilioDto.class);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo registrar el domicilio.", ex);
        }
    }

    public Optional<DomicilioDto> update(Long id, DomicilioDto domicilio) {
        HttpEntity<DomicilioDto> request = new HttpEntity<>(domicilio);
        try {
            ResponseEntity<DomicilioDto> response = restTemplate.exchange(
                    domiciliosUrl + "/" + id,
                    HttpMethod.PUT,
                    request,
                    DomicilioDto.class);
            return Optional.ofNullable(response.getBody());
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar el domicilio con id " + id + ".", ex);
        }
    }

    public void deleteById(Long id) {
        try {
            restTemplate.delete(domiciliosUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar el domicilio con id " + id + ".", ex);
        }
    }
}
