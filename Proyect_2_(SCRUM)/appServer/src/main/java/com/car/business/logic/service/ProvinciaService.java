package com.car.business.logic.service;

import com.car.business.domain.Provincia;
import com.car.business.dto.ProvinciaDto;
import com.car.business.mappers.ProvinciaMapper;
import com.car.business.percistence.repository.ProvinciaRepository;
import org.springframework.stereotype.Service;

@Service
public class ProvinciaService extends BaseService<Provincia, ProvinciaDto, String> {

    public ProvinciaService(ProvinciaRepository repository, ProvinciaMapper mapper) {
        super(repository, mapper);
    }
}
