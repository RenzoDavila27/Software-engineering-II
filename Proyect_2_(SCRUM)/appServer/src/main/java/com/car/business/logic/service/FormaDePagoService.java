package com.car.business.logic.service;

import com.car.business.domain.FormaDePago;
import com.car.business.percistence.repository.FormaDePagoRepository;
import org.springframework.stereotype.Service;

@Service
public class FormaDePagoService extends BaseService<FormaDePago, String> {

    public FormaDePagoService(FormaDePagoRepository repository) {
        super(repository);
    }
}
