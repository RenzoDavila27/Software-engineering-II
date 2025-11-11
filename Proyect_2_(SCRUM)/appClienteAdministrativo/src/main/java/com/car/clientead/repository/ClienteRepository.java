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

import com.car.clientead.client.dto.ClienteDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class ClienteRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/clientes";

    public ClienteRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ClienteDto> findAll() {
        try {
            ResponseEntity<List<ClienteDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ClienteDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de clientes.", ex);
        }
    }

    public ClienteDto findById(String id) {
        try {
            ResponseEntity<ClienteDto> response = restTemplate.getForEntity(
                    baseUrl + "/" + id,
                    ClienteDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el cliente con ID: " + id, ex);
        }
    }

    public ClienteDto create(ClienteDto dto) {
        try {
            ResponseEntity<ClienteDto> response = restTemplate.postForEntity(
                    baseUrl,
                    dto,
                    ClienteDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo crear el cliente.", ex);
        }
    }

    public ClienteDto update(String id, ClienteDto dto) {
        try {
            HttpEntity<ClienteDto> requestEntity = new HttpEntity<>(dto);
            ResponseEntity<ClienteDto> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    requestEntity,
                    ClienteDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar el cliente con ID: " + id, ex);
        }
    }

    public void delete(String id) {
        try {
            restTemplate.delete(baseUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar el cliente con ID: " + id, ex);
        }
    }
}
