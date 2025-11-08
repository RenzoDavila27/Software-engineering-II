package com.car.business.logic.service;

import com.car.business.domain.Vehiculo;
import com.car.business.percistence.repository.VehiculoRepository;
import org.springframework.stereotype.Service;

@Service
public class VehiculoService extends BaseService<Vehiculo, String> {

    public VehiculoService(VehiculoRepository repository) {
        super(repository);
    }
}
