package com.car.business.logic.service;

import com.car.business.domain.Nacionalidad;
import com.car.business.dto.NacionalidadDto;
import com.car.business.mappers.NacionalidadMapper;
import com.car.business.percistence.repository.NacionalidadRepository;
import org.springframework.stereotype.Service;

@Service
public class NacionalidadService extends BaseService<Nacionalidad, NacionalidadDto, String> {

    public NacionalidadService(NacionalidadRepository repository, NacionalidadMapper mapper) {
        super(repository, mapper);
    }
}
