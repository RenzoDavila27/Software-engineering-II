package com.example.frontend.business.persistence.rest;

import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.example.frontend.business.domain.EstudioDto;
import com.example.frontend.business.logic.service.dto.EstudioRequest;
import com.example.frontend.config.FrontendProperties;

@Repository
public class EstudioDAORest extends BaseRestDAO<EstudioDto, Long, EstudioRequest> {

    public EstudioDAORest(RestTemplate restTemplate, FrontendProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String resourcePath() {
        return "/api/v1/estudio";
    }

    @Override
    protected String listarPath() {
        return "/listarEstudio";
    }

    @Override
    protected Class<EstudioDto> responseClass() {
        return EstudioDto.class;
    }

    @Override
    protected Class<EstudioDto[]> responseArrayClass() {
        return EstudioDto[].class;
    }
}
