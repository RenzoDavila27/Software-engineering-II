package com.car.business.logic.service;

import com.car.business.domain.CostoVehiculo;
import com.car.business.percistence.repository.CostoVehiculoRepository;
import org.springframework.stereotype.Service;

@Service
public class CostoVehiculoService extends BaseService<CostoVehiculo, String> {

    public CostoVehiculoService(CostoVehiculoRepository repository) {
        super(repository);
    }
}
