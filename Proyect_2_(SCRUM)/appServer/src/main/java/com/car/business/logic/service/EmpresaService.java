package com.car.business.logic.service;

import com.car.business.domain.Empresa;
import com.car.business.percistence.repository.EmpresaRepository;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService extends BaseService<Empresa, String> {

    public EmpresaService(EmpresaRepository repository) {
        super(repository);
    }
}
