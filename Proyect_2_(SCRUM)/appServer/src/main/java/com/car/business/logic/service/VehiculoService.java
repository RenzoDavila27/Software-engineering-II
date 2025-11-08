package com.car.business.logic.service;

import com.car.business.domain.Vehiculo;
import com.car.business.dto.VehiculoDto;
import com.car.business.mappers.VehiculoMapper;
import com.car.business.percistence.repository.VehiculoRepository;
import org.springframework.stereotype.Service;

@Service
public class VehiculoService extends BaseService<Vehiculo, VehiculoDto, String> {

    public VehiculoService(VehiculoRepository repository, VehiculoMapper mapper) {
        super(repository, mapper);
    }
}
