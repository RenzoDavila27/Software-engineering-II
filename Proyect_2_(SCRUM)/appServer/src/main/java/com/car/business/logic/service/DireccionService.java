package com.car.business.logic.service;

import com.car.business.domain.Direccion;
import com.car.business.dto.DireccionDto;
import com.car.business.mappers.DireccionMapper;
import com.car.business.percistence.repository.DireccionRepository;
import org.springframework.stereotype.Service;

@Service
public class DireccionService extends BaseService<Direccion, DireccionDto, String> {

    public DireccionService(DireccionRepository repository, DireccionMapper mapper) {
        super(repository, mapper);
    }
}
