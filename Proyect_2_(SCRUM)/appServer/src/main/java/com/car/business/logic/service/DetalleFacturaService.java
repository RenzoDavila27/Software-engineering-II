package com.car.business.logic.service;

import com.car.business.domain.DetalleFactura;
import com.car.business.percistence.repository.DetalleFacturaRepository;
import org.springframework.stereotype.Service;

@Service
public class DetalleFacturaService extends BaseService<DetalleFactura, String> {

    public DetalleFacturaService(DetalleFacturaRepository repository) {
        super(repository);
    }
}
