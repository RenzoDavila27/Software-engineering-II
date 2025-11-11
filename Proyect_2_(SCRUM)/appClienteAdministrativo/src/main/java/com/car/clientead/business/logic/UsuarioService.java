package com.car.clientead.business.logic;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.car.clientead.client.dto.UsuarioDto;
import com.car.clientead.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public List<UsuarioDto> listar() {
        return repository.findAll().stream()
                .filter(this::usuarioValido)
                .collect(Collectors.toList());
    }

    public UsuarioDto consultar(String id) {
        return repository.findById(id);
    }

    public UsuarioDto crear(UsuarioDto dto) {
        validar(dto);
        return repository.create(dto);
    }

    public UsuarioDto modificar(String id, UsuarioDto dto) {
        validar(dto);
        return repository.update(id, dto);
    }

    public void eliminar(String id) {
        repository.delete(id);
    }

    private void validar(UsuarioDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos del usuario no pueden ser nulos.");
        }
        if (!StringUtils.hasText(dto.getNombreUsuario())) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (!StringUtils.hasText(dto.getClave())) {
            throw new IllegalArgumentException("La clave es obligatoria.");
        }
        if (dto.getRolUsuario() == null) {
            throw new IllegalArgumentException("Debe seleccionar un rol para el usuario.");
        }
        if (!StringUtils.hasText(dto.getPersonaId())) {
            throw new IllegalArgumentException("Debe seleccionar una persona asociada.");
        }
    }

    private boolean usuarioValido(UsuarioDto dto) {
        return dto != null && StringUtils.hasText(dto.getNombreUsuario());
    }
}
