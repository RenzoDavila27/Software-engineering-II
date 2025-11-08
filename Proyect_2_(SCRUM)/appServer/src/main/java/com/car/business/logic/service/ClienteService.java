package com.car.business.logic.service;

import com.car.business.domain.Cliente;
import com.car.business.percistence.repository.ClienteRepository;
import org.springframework.stereotype.Service;

@Service
public class ClienteService extends BaseService<Cliente, String> {

    public ClienteService(ClienteRepository repository) {
        super(repository);
    }
}
