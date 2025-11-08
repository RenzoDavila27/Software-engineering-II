package com.car.business.logic.service;

import com.car.business.domain.Factura;
import com.car.business.percistence.repository.FacturaRepository;
import org.springframework.stereotype.Service;

@Service
public class FacturaService extends BaseService<Factura, String> {

    public FacturaService(FacturaRepository repository) {
        super(repository);
    }
}
