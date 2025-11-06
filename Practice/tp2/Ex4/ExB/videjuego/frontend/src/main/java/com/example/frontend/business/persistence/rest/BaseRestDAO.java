package com.example.frontend.business.persistence.rest;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.client.RestTemplate;

import com.example.frontend.business.logic.error.ErrorServiceException;
import com.example.frontend.config.FrontendProperties;

public abstract class BaseRestDAO<T, ID, REQUEST> {

    private final RestTemplate restTemplate;
    private final FrontendProperties properties;

    protected BaseRestDAO(RestTemplate restTemplate, FrontendProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public List<T> listar() throws ErrorServiceException {
        try {
            T[] response = restTemplate.getForObject(basePath() + listarPath(), responseArrayClass());
            return response == null ? List.of() : Arrays.asList(response);
        } catch (Exception ex) {
            throw wrapException(ex);
        }
    }

    public T buscar(ID id) throws ErrorServiceException {
        try {
            return restTemplate.getForObject(basePath() + "/" + id, responseClass());
        } catch (Exception ex) {
            throw wrapException(ex);
        }
    }

    public void crear(REQUEST request) throws ErrorServiceException {
        try {
            restTemplate.postForEntity(basePath(), request, Void.class);
        } catch (Exception ex) {
            throw wrapException(ex);
        }
    }

    public void actualizar(ID id, REQUEST request) throws ErrorServiceException {
        try {
            restTemplate.put(basePath() + "/" + id, request);
        } catch (Exception ex) {
            throw wrapException(ex);
        }
    }

    public void eliminar(ID id) throws ErrorServiceException {
        try {
            restTemplate.delete(basePath() + "/" + id);
        } catch (Exception ex) {
            throw wrapException(ex);
        }
    }

    protected ErrorServiceException wrapException(Exception ex) {
        return new ErrorServiceException("Error de Sistemas");
    }

    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    protected String basePath() {
        return properties.getBaseUrl() + resourcePath();
    }

    protected abstract String resourcePath();

    protected abstract String listarPath();

    protected abstract Class<T> responseClass();

    protected abstract Class<T[]> responseArrayClass();
}
