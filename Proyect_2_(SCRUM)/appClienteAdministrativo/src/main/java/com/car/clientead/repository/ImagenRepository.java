package com.car.clientead.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.car.clientead.client.dto.ImagenDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class ImagenRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/imagenes";

    public ImagenRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ImagenDto> findAll() {
        try {
            ResponseEntity<List<ImagenDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ImagenDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de imágenes.", ex);
        }
    }

    public ImagenDto findById(String id) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + id, ImagenDto.class);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener la imagen con ID: " + id, ex);
        }
    }
}
