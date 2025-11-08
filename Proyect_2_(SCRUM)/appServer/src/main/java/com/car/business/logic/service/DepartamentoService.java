package com.car.business.logic.service;

import com.car.business.domain.Departamento;
import com.car.business.dto.DepartamentoDto;
import com.car.business.mappers.DepartamentoMapper;
import com.car.business.percistence.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;

@Service
public class DepartamentoService extends BaseService<Departamento, DepartamentoDto, String> {

    public DepartamentoService(DepartamentoRepository repository, DepartamentoMapper mapper) {
        super(repository, mapper);
    }
}
