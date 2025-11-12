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

import com.car.clientead.client.dto.ContactoTelefonicoDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class ContactoTelefonicoRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/contactos-telefonicos";

    public ContactoTelefonicoRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ContactoTelefonicoDto> findAll() {
        try {
            ResponseEntity<List<ContactoTelefonicoDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ContactoTelefonicoDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener los contactos telefónicos.", ex);
        }
    }

    public ContactoTelefonicoDto findById(String id) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + id, ContactoTelefonicoDto.class);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el contacto telefónico con ID: " + id, ex);
        }
    }

    public ContactoTelefonicoDto create(ContactoTelefonicoDto dto) {
        try {
            ResponseEntity<ContactoTelefonicoDto> response = restTemplate.postForEntity(
                    baseUrl,
                    dto,
                    ContactoTelefonicoDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo crear el contacto telefónico.", ex);
        }
    }

    public ContactoTelefonicoDto update(String id, ContactoTelefonicoDto dto) {
        try {
            HttpEntity<ContactoTelefonicoDto> requestEntity = new HttpEntity<>(dto);
            ResponseEntity<ContactoTelefonicoDto> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    requestEntity,
                    ContactoTelefonicoDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar el contacto telefónico con ID: " + id, ex);
        }
    }

    public void delete(String id) {
        try {
            restTemplate.delete(baseUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar el contacto telefónico con ID: " + id, ex);
        }
    }
}
