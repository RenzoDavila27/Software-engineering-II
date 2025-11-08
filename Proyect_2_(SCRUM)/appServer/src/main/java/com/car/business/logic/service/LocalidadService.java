package com.car.business.logic.service;

import com.car.business.domain.Localidad;
import com.car.business.percistence.repository.LocalidadRepository;
import org.springframework.stereotype.Service;

@Service
public class LocalidadService extends BaseService<Localidad, String> {

    public LocalidadService(LocalidadRepository repository) {
        super(repository);
    }
}
