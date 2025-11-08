package com.car.business.logic.service;

import com.car.business.domain.CaracteristicaVehiculo;
import com.car.business.percistence.repository.CaracteristicaVehiculoRepository;
import org.springframework.stereotype.Service;

@Service
public class CaracteristicaVehiculoService extends BaseService<CaracteristicaVehiculo, String> {

    public CaracteristicaVehiculoService(CaracteristicaVehiculoRepository repository) {
        super(repository);
    }
}
