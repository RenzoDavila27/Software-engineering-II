package com.example.frontend.business.persistence.rest;

import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.example.frontend.business.domain.VideojuegoDto;
import com.example.frontend.business.logic.service.dto.VideojuegoRequest;
import com.example.frontend.config.FrontendProperties;

@Repository
public class VideojuegoDAORest extends BaseRestDAO<VideojuegoDto, Long, VideojuegoRequest> {

    public VideojuegoDAORest(RestTemplate restTemplate, FrontendProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String resourcePath() {
        return "/api/v1/videojuego";
    }

    @Override
    protected String listarPath() {
        return "/listarVideojuego";
    }

    @Override
    protected Class<VideojuegoDto> responseClass() {
        return VideojuegoDto.class;
    }

    @Override
    protected Class<VideojuegoDto[]> responseArrayClass() {
        return VideojuegoDto[].class;
    }
}
