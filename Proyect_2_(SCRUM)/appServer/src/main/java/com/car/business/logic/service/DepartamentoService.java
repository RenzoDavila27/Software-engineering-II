package com.car.business.logic.service;

import com.car.business.domain.Departamento;
import com.car.business.percistence.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;

@Service
public class DepartamentoService extends BaseService<Departamento, String> {

    public DepartamentoService(DepartamentoRepository repository) {
        super(repository);
    }
}
