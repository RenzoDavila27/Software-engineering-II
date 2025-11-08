package com.car.business.logic.service;

import com.car.business.domain.Alquiler;
import com.car.business.percistence.repository.AlquilerRepository;
import org.springframework.stereotype.Service;

@Service
public class AlquilerService extends BaseService<Alquiler, String> {

    public AlquilerService(AlquilerRepository repository) {
        super(repository);
    }
}
