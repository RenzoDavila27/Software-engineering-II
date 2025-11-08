package com.car.business.logic.service;

import com.car.business.domain.Empleado;
import com.car.business.dto.EmpleadoDto;
import com.car.business.mappers.EmpleadoMapper;
import com.car.business.percistence.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoService extends BaseService<Empleado, EmpleadoDto, String> {

    public EmpleadoService(EmpleadoRepository repository, EmpleadoMapper mapper) {
        super(repository, mapper);
    }
}
