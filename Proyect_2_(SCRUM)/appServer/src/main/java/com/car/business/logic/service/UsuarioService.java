package com.car.business.logic.service;

import com.car.business.domain.Usuario;
import com.car.business.percistence.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService extends BaseService<Usuario, String> {

    public UsuarioService(UsuarioRepository repository) {
        super(repository);
    }
}
