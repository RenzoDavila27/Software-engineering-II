package com.car.business.logic.service;

import com.car.business.domain.Empleado;
import com.car.business.percistence.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

@Service
public class EmpleadoService extends BaseService<Empleado, String> {

    public EmpleadoService(EmpleadoRepository repository) {
        super(repository);
    }
}
