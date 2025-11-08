package com.car.business.logic.service;

import com.car.business.domain.Factura;
import com.car.business.dto.FacturaDto;
import com.car.business.mappers.FacturaMapper;
import com.car.business.percistence.repository.FacturaRepository;
import org.springframework.stereotype.Service;

@Service
public class FacturaService extends BaseService<Factura, FacturaDto, String> {

    public FacturaService(FacturaRepository repository, FacturaMapper mapper) {
        super(repository, mapper);
    }
}
