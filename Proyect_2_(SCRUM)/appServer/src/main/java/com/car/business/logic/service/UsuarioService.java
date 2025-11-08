package com.car.business.logic.service;

import com.car.business.domain.Usuario;
import com.car.business.dto.UsuarioDto;
import com.car.business.mappers.UsuarioMapper;
import com.car.business.percistence.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService extends BaseService<Usuario, UsuarioDto, String> {

    public UsuarioService(UsuarioRepository repository, UsuarioMapper mapper) {
        super(repository, mapper);
    }
}
