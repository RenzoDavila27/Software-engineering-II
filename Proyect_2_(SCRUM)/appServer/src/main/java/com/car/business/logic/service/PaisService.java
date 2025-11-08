package com.car.business.logic.service;

import com.car.business.domain.Pais;
import com.car.business.percistence.repository.PaisRepository;
import org.springframework.stereotype.Service;

@Service
public class PaisService extends BaseService<Pais, String> {

    public PaisService(PaisRepository repository) {
        super(repository);
    }
}
