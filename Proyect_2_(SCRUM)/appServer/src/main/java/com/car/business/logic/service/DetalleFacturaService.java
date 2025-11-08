package com.car.business.logic.service;

import com.car.business.domain.DetalleFactura;
import com.car.business.dto.DetalleFacturaDto;
import com.car.business.mappers.DetalleFacturaMapper;
import com.car.business.percistence.repository.DetalleFacturaRepository;
import org.springframework.stereotype.Service;

@Service
public class DetalleFacturaService extends BaseService<DetalleFactura, DetalleFacturaDto, String> {

    public DetalleFacturaService(DetalleFacturaRepository repository, DetalleFacturaMapper mapper) {
        super(repository, mapper);
    }
}
