package com.car.business.logic.service;

import com.car.business.domain.Usuario;
import com.car.business.dto.UsuarioDto;
import com.car.business.logic.error.BusinessException;
import com.car.business.mappers.UsuarioMapper;
import com.car.business.percistence.repository.UsuarioRepository;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UsuarioService extends BaseService<Usuario, UsuarioDto, String> {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository repository, UsuarioMapper mapper, PasswordEncoder passwordEncoder) {
        super(repository, mapper);
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = repository;
    }

    public Usuario obtenerPorNombreUsuario(String nombreUsuario) {
        if (!StringUtils.hasText(nombreUsuario)) {
            throw new BusinessException("El nombre de usuario es obligatorio.");
        }
        return usuarioRepository.findByNombreUsuarioAndEliminadoFalse(nombreUsuario)
            .orElseThrow(() -> new BusinessException("No se encontró el usuario solicitado."));
    }

    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        if (!StringUtils.hasText(nombreUsuario)) {
            return Optional.empty();
        }
        return usuarioRepository.findByNombreUsuarioAndEliminadoFalse(nombreUsuario);
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

    @Override
    protected void preAlta(Usuario entidad) {
        codificarClave(entidad);
    }

    @Override
    protected void preModificacion(Usuario entidad) {
        codificarClave(entidad);
    }

    private void codificarClave(Usuario entidad) {
        String clave = entidad.getClave();
        if (StringUtils.hasText(clave) && !estaCodificada(clave)) {
            entidad.setClave(passwordEncoder.encode(clave));
        }
    }

    private boolean estaCodificada(String clave) {
        return clave.startsWith("$2a$") || clave.startsWith("$2b$") || clave.startsWith("$2y$");
    }
}
