package com.car.business.logic.service;

import com.car.business.domain.Usuario;
import com.car.business.dto.UsuarioDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.UsuarioMapper;
import com.car.business.percistence.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UsuarioService extends BaseService<Usuario, UsuarioDto, String> {

    public UsuarioService(UsuarioRepository repository, UsuarioMapper mapper) {
        super(repository, mapper);
    }

    @Override
    protected void validar(Usuario entidad) throws BusinessException {
        if (entidad == null) {
            throw new BusinessException("El usuario es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getNombreUsuario())) {
            throw new BusinessException("El nombre de usuario es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getClave())) {
            throw new BusinessException("La clave es obligatoria.");
        }
        if (entidad.getRolUsuario() == null) {
            throw new BusinessException("El rol es obligatorio.");
        }
        if (entidad.getPersona() == null) {
            throw new BusinessException("La persona asociada es obligatoria.");
        }
    }
}
