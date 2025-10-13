package com.books.demo.repository;

import com.books.demo.client.dto.AutorDto;
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
public class AutorRepository {

    private final RestTemplate restTemplate;
    private final String autoresUrl = "http://localhost:8080/api/autores";

    public AutorRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<AutorDto> findAll() {
        try {
            ResponseEntity<List<AutorDto>> response = restTemplate.exchange(
                    autoresUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<AutorDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de autores.", ex);
        }
    }

    public Optional<AutorDto> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            AutorDto autor = restTemplate.getForObject(autoresUrl + "/" + id, AutorDto.class);
            return Optional.ofNullable(autor);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (HttpClientErrorException ex) {
            throw new ApiClientException("Error al consultar el autor con id " + id + ".", ex);
        } catch (RestClientException ex) {
            throw new ApiClientException("Error al consultar el autor con id " + id + ".", ex);
        }
    }

    public AutorDto save(AutorDto autor) {
        try {
            return restTemplate.postForObject(autoresUrl, autor, AutorDto.class);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo registrar el autor.", ex);
        }
    }

    public Optional<AutorDto> update(Long id, AutorDto autor) {
        HttpEntity<AutorDto> request = new HttpEntity<>(autor);
        try {
            ResponseEntity<AutorDto> response = restTemplate.exchange(
                    autoresUrl + "/" + id,
                    HttpMethod.PUT,
                    request,
                    AutorDto.class);
            return Optional.ofNullable(response.getBody());
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar el autor con id " + id + ".", ex);
        }
    }

    public void deleteById(Long id) {
        try {
            restTemplate.delete(autoresUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar el autor con id " + id + ".", ex);
        }
    }
}
