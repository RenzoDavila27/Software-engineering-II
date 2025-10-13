package com.books.demo.repository;

import com.books.demo.client.dto.PersonaDto;
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
public class PersonaRepository {

    private final RestTemplate restTemplate;
    private final String personasUrl = "http://localhost:8080/api/personas";

    public PersonaRepository(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PersonaDto> findAll() {
        try {
            ResponseEntity<List<PersonaDto>> response = restTemplate.exchange(
                    personasUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<PersonaDto>>() {});
            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo obtener el listado de personas.", ex);
        }
    }

    public Optional<PersonaDto> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            PersonaDto persona = restTemplate.getForObject(personasUrl + "/" + id, PersonaDto.class);
            return Optional.ofNullable(persona);
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        } catch (HttpClientErrorException ex) {
            throw new ApiClientException("Error al consultar la persona con id " + id + ".", ex);
        } catch (RestClientException ex) {
            throw new ApiClientException("Error al consultar la persona con id " + id + ".", ex);
        }
    }

    public PersonaDto save(PersonaDto persona) {
        try {
            return restTemplate.postForObject(personasUrl, persona, PersonaDto.class);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo registrar la persona.", ex);
        }
    }

    public Optional<PersonaDto> update(Long id, PersonaDto persona) {
        HttpEntity<PersonaDto> request = new HttpEntity<>(persona);
        try {
            ResponseEntity<PersonaDto> response = restTemplate.exchange(
                    personasUrl + "/" + id,
                    HttpMethod.PUT,
                    request,
                    PersonaDto.class);
            return Optional.ofNullable(response.getBody());
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo actualizar la persona con id " + id + ".", ex);
        }
    }

    public void deleteById(Long id) {
        try {
            restTemplate.delete(personasUrl + "/" + id);
        } catch (RestClientException ex) {
            throw new ApiClientException("No se pudo eliminar la persona con id " + id + ".", ex);
        }
    }
}
