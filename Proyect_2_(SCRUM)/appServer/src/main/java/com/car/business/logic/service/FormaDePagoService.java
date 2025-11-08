package com.car.business.logic.service;

import com.car.business.domain.FormaDePago;
import com.car.business.dto.FormaDePagoDto;
import com.car.business.mappers.FormaDePagoMapper;
import com.car.business.percistence.repository.FormaDePagoRepository;
import org.springframework.stereotype.Service;

@Service
public class FormaDePagoService extends BaseService<FormaDePago, FormaDePagoDto, String> {

    public FormaDePagoService(FormaDePagoRepository repository, FormaDePagoMapper mapper) {
        super(repository, mapper);
    }
}
