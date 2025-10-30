package com.books.demo.repository;

import com.books.demo.client.dto.LibroDto;
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
public class LibroRepository {

    private final RestTemplate restTemplate;
    private final String librosUrl = "http://localhost:8080/api/libros";

    public LibroRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<LibroDto> findAll() {
        try {
            ResponseEntity<List<LibroDto>> response = restTemplate.exchange(
                    librosUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<LibroDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de libros.", ex);
        }
    }

    public Optional<LibroDto> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            LibroDto libro = restTemplate.getForObject(librosUrl + "/" + id, LibroDto.class);
            return Optional.ofNullable(libro);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (HttpClientErrorException ex) {
            throw new ApiClientException("Error al consultar el libro con id " + id + ".", ex);
        } catch (RestClientException ex) {
            throw new ApiClientException("Error al consultar el libro con id " + id + ".", ex);
        }
    }

    public Optional<LibroDto> update(Long id, LibroDto libro) {
        HttpEntity<LibroDto> request = new HttpEntity<>(libro);
        try {
            ResponseEntity<LibroDto> response = restTemplate.exchange(
                    librosUrl + "/" + id,
                    HttpMethod.PUT,
                    request,
                    LibroDto.class);
            return Optional.ofNullable(response.getBody());
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar el libro con id " + id + ".", ex);
        }
    }

    public LibroDto save(LibroDto libro) {
        try {
            return restTemplate.postForObject(librosUrl, libro, LibroDto.class);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo registrar el libro.", ex);
        }
    }

    public void deleteById(Long id) {
        try {
            restTemplate.delete(librosUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar el libro con id " + id + ".", ex);
        }
    }
}
