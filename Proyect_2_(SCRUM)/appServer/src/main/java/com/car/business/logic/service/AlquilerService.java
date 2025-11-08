package com.car.business.logic.service;

import com.car.business.domain.Alquiler;
import com.car.business.dto.AlquilerDto;
import com.car.business.mappers.AlquilerMapper;
import com.car.business.percistence.repository.AlquilerRepository;
import org.springframework.stereotype.Service;

@Service
public class AlquilerService extends BaseService<Alquiler, AlquilerDto, String> {

    public AlquilerService(AlquilerRepository repository, AlquilerMapper mapper) {
        super(repository, mapper);
    }
}
