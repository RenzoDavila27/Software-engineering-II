package com.books.demo.repository;

import com.books.demo.client.dto.LocalidadDto;
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
public class LocalidadRepository {

    private final RestTemplate restTemplate;
    private final String localidadesUrl = "http://localhost:8080/api/localidades";

    public LocalidadRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<LocalidadDto> findAll() {
        try {
            ResponseEntity<List<LocalidadDto>> response = restTemplate.exchange(
                    localidadesUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<LocalidadDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de localidades.", ex);
        }
    }

    public Optional<LocalidadDto> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            LocalidadDto localidad = restTemplate.getForObject(localidadesUrl + "/" + id, LocalidadDto.class);
            return Optional.ofNullable(localidad);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new ApiClientException("Error al consultar la localidad con id " + id + ".", ex);
        }
    }

    public LocalidadDto save(LocalidadDto localidad) {
        try {
            return restTemplate.postForObject(localidadesUrl, localidad, LocalidadDto.class);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo registrar la localidad.", ex);
        }
    }

    public Optional<LocalidadDto> update(Long id, LocalidadDto localidad) {
        HttpEntity<LocalidadDto> request = new HttpEntity<>(localidad);
        try {
            ResponseEntity<LocalidadDto> response = restTemplate.exchange(
                    localidadesUrl + "/" + id,
                    HttpMethod.PUT,
                    request,
                    LocalidadDto.class);
            return Optional.ofNullable(response.getBody());
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar la localidad con id " + id + ".", ex);
        }
    }

    public void deleteById(Long id) {
        try {
            restTemplate.delete(localidadesUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar la localidad con id " + id + ".", ex);
        }
    }
}
