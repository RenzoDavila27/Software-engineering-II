package com.car.business.logic.service;

import com.car.business.domain.Cliente;
import com.car.business.dto.ClienteDto;
import com.car.business.mappers.ClienteMapper;
import com.car.business.percistence.repository.ClienteRepository;
import org.springframework.stereotype.Service;

@Service
public class ClienteService extends BaseService<Cliente, ClienteDto, String> {

    public ClienteService(ClienteRepository repository, ClienteMapper mapper) {
        super(repository, mapper);
    }
}
