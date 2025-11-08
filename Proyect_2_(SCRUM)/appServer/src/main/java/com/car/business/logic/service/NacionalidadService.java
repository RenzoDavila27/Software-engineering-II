package com.car.business.logic.service;

import com.car.business.domain.Nacionalidad;
import com.car.business.percistence.repository.NacionalidadRepository;
import org.springframework.stereotype.Service;

@Service
public class NacionalidadService extends BaseService<Nacionalidad, String> {

    public NacionalidadService(NacionalidadRepository repository) {
        super(repository);
    }
}
