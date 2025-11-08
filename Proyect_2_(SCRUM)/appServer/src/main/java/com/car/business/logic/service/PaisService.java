package com.car.business.logic.service;

import com.car.business.domain.Pais;
import com.car.business.dto.PaisDto;
import com.car.business.mappers.PaisMapper;
import com.car.business.percistence.repository.PaisRepository;
import org.springframework.stereotype.Service;

@Service
public class PaisService extends BaseService<Pais, PaisDto, String> {

    public PaisService(PaisRepository repository, PaisMapper mapper) {
        super(repository, mapper);
    }
}
