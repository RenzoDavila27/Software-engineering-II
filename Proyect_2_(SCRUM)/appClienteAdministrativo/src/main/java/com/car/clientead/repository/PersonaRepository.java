package com.car.clientead.repository;

import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.car.clientead.client.dto.PersonaDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class PersonaRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/personas";

    public PersonaRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PersonaDto> findAll() {
        try {
            ResponseEntity<List<PersonaDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PersonaDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de personas.", ex);
        }
    }

    public PersonaDto findById(String id) {
        try {
            ResponseEntity<PersonaDto> response = restTemplate.getForEntity(
                    baseUrl + "/" + id,
                    PersonaDto.class);
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener la persona con ID: " + id, ex);
        }
    }
}
