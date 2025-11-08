package com.car.business.logic.service;

import com.car.business.domain.Direccion;
import com.car.business.percistence.repository.DireccionRepository;
import org.springframework.stereotype.Service;

@Service
public class DireccionService extends BaseService<Direccion, String> {

    public DireccionService(DireccionRepository repository) {
        super(repository);
    }
}
