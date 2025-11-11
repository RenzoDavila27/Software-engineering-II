package com.car.clientead.repository;

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

    public ImagenDto findById(String id) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + id, ImagenDto.class);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener la imagen con ID: " + id, ex);
        }
    }
}
