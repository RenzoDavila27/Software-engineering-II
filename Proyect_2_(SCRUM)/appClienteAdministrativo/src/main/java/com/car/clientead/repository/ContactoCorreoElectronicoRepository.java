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

import com.car.clientead.client.dto.ContactoCorreoElectronicoDto;
import com.car.clientead.client.exception.ApiClientException;

@Repository
public class ContactoCorreoElectronicoRepository {

    private final RestTemplate restTemplate;
    private final String baseUrl = "http://localhost:8080/api/contactos-correo";

    public ContactoCorreoElectronicoRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ContactoCorreoElectronicoDto> findAll() {
        try {
            ResponseEntity<List<ContactoCorreoElectronicoDto>> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ContactoCorreoElectronicoDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener los contactos de correo.", ex);
        }
    }

    public ContactoCorreoElectronicoDto findById(String id) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + id, ContactoCorreoElectronicoDto.class);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el contacto de correo con ID: " + id, ex);
        }
    }

    public ContactoCorreoElectronicoDto create(ContactoCorreoElectronicoDto dto) {
        try {
            ResponseEntity<ContactoCorreoElectronicoDto> response = restTemplate.postForEntity(
                    baseUrl,
                    dto,
                    ContactoCorreoElectronicoDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo crear el contacto de correo.", ex);
        }
    }

    public ContactoCorreoElectronicoDto update(String id, ContactoCorreoElectronicoDto dto) {
        try {
            HttpEntity<ContactoCorreoElectronicoDto> requestEntity = new HttpEntity<>(dto);
            ResponseEntity<ContactoCorreoElectronicoDto> response = restTemplate.exchange(
                    baseUrl + "/" + id,
                    HttpMethod.PUT,
                    requestEntity,
                    ContactoCorreoElectronicoDto.class
            );
            return response.getBody();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar el contacto de correo con ID: " + id, ex);
        }
    }

    public void delete(String id) {
        try {
            restTemplate.delete(baseUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar el contacto de correo con ID: " + id, ex);
        }
    }
}
