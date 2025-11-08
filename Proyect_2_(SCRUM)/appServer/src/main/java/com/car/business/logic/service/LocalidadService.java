package com.car.business.logic.service;

import com.car.business.domain.Localidad;
import com.car.business.dto.LocalidadDto;
import com.car.business.mappers.LocalidadMapper;
import com.car.business.percistence.repository.LocalidadRepository;
import org.springframework.stereotype.Service;

@Service
public class LocalidadService extends BaseService<Localidad, LocalidadDto, String> {

    public LocalidadService(LocalidadRepository repository, LocalidadMapper mapper) {
        super(repository, mapper);
    }
}
