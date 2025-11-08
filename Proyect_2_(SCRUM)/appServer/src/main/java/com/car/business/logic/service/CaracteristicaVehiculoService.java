package com.car.business.logic.service;

import com.car.business.domain.CaracteristicaVehiculo;
import com.car.business.dto.CaracteristicaVehiculoDto;
import com.car.business.mappers.CaracteristicaVehiculoMapper;
import com.car.business.percistence.repository.CaracteristicaVehiculoRepository;
import org.springframework.stereotype.Service;

@Service
public class CaracteristicaVehiculoService extends BaseService<CaracteristicaVehiculo, CaracteristicaVehiculoDto, String> {

    public CaracteristicaVehiculoService(CaracteristicaVehiculoRepository repository, CaracteristicaVehiculoMapper mapper) {
        super(repository, mapper);
    }
}
