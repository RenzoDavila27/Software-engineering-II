package com.car.business.logic.service;

import com.car.business.domain.CostoVehiculo;
import com.car.business.dto.CostoVehiculoDto;
import com.car.business.mappers.CostoVehiculoMapper;
import com.car.business.percistence.repository.CostoVehiculoRepository;
import org.springframework.stereotype.Service;

@Service
public class CostoVehiculoService extends BaseService<CostoVehiculo, CostoVehiculoDto, String> {

    public CostoVehiculoService(CostoVehiculoRepository repository, CostoVehiculoMapper mapper) {
        super(repository, mapper);
    }
}
