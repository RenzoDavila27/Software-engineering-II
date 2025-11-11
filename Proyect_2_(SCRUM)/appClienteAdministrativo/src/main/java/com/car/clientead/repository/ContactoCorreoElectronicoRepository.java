package com.car.clientead.repository;

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

    public ContactoCorreoElectronicoDto findById(String id) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + id, ContactoCorreoElectronicoDto.class);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el contacto de correo con ID: " + id, ex);
        }
    }
}
