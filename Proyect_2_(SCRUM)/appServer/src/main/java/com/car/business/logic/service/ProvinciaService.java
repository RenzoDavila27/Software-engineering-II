package com.car.business.logic.service;

import com.car.business.domain.Provincia;
import com.car.business.percistence.repository.ProvinciaRepository;
import org.springframework.stereotype.Service;

@Service
public class ProvinciaService extends BaseService<Provincia, String> {

    public ProvinciaService(ProvinciaRepository repository) {
        super(repository);
    }
}
