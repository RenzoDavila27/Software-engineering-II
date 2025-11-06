package com.example.frontend.business.persistence.rest;

import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.example.frontend.business.domain.CategoriaDto;
import com.example.frontend.business.logic.service.dto.CategoriaRequest;
import com.example.frontend.config.FrontendProperties;

@Repository
public class CategoriaDAORest extends BaseRestDAO<CategoriaDto, Long, CategoriaRequest> {

    public CategoriaDAORest(RestTemplate restTemplate, FrontendProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String resourcePath() {
        return "/api/v1/categoria";
    }

    @Override
    protected String listarPath() {
        return "/listarCategoria";
    }

    @Override
    protected Class<CategoriaDto> responseClass() {
        return CategoriaDto.class;
    }

    @Override
    protected Class<CategoriaDto[]> responseArrayClass() {
        return CategoriaDto[].class;
    }
}
