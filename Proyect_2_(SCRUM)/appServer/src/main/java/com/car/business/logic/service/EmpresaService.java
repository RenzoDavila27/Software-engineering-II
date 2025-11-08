package com.car.business.logic.service;

import com.car.business.domain.Empresa;
import com.car.business.dto.EmpresaDto;
import com.car.business.mappers.EmpresaMapper;
import com.car.business.percistence.repository.EmpresaRepository;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService extends BaseService<Empresa, EmpresaDto, String> {

    public EmpresaService(EmpresaRepository repository, EmpresaMapper mapper) {
        super(repository, mapper);
    }
}
