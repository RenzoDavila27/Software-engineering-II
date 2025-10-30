package com.contactos.business.logic.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.contactos.business.domain.Persona;
import com.contactos.business.domain.Usuario;
import com.contactos.business.domain.enumeration.Rol;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.persistence.repository.PersonaRepository;
import com.contactos.business.persistence.repository.UsuarioRepository;

@Service
public class UsuarioService extends BaseService<Usuario, Long> implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PersonaRepository personaRepository,
                          PasswordEncoder passwordEncoder) {
        super(usuarioRepository);
        this.usuarioRepository = usuarioRepository;
        this.personaRepository = personaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Usuario entidad) throws ErrorServiceException {
        if (entidad == null) {
            throw new ErrorServiceException("El usuario es requerido");
        }
        if (entidad.getCuenta() == null || entidad.getCuenta().isBlank()) {
            throw new ErrorServiceException("La cuenta del usuario es requerida");
        }
        if (useCase == BaseUseCaseService.ALTA &&
            (entidad.getClave() == null || entidad.getClave().isBlank())) {
            throw new ErrorServiceException("La clave es obligatoria para crear un usuario");
        }
        if (entidad.getPersona() != null && entidad.getPersona().getId() == null) {
            throw new ErrorServiceException("La persona asociada al usuario debe ser válida");
        }
    }

    @Override
    protected void preAlta(Usuario entidad) throws ErrorServiceException {
        prepararUsuario(entidad);
        if (entidad.getRol() == null) {
            entidad.setRol(Rol.USER);
        }
        entidad.setClave(passwordEncoder.encode(entidad.getClave()));
    }

    @Override
    protected void preModificacion(Usuario entidad) throws ErrorServiceException {
        prepararUsuario(entidad);
        if (entidad.getClave() != null && !entidad.getClave().isBlank()) {
            entidad.setClave(passwordEncoder.encode(entidad.getClave()));
        } else {
            usuarioRepository.findById(entidad.getId())
                    .ifPresent(actual -> entidad.setClave(actual.getClave()));
        }
        if (entidad.getRol() == null) {
            usuarioRepository.findById(entidad.getId())
                    .map(Usuario::getRol)
                    .ifPresent(entidad::setRol);
        }
    }

    private void prepararUsuario(Usuario entidad) throws ErrorServiceException {
        if (entidad.getPersona() != null && entidad.getPersona().getId() != null) {
            Persona persona = personaRepository.findById(entidad.getPersona().getId())
                    .filter(p -> !Boolean.TRUE.equals(p.isEliminado()))
                    .orElseThrow(() -> new ErrorServiceException("La persona asociada no existe o está eliminada"));
            entidad.setPersona(persona);
        } else {
            entidad.setPersona(null);
        }
        entidad.setEliminado(Boolean.FALSE);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCuenta(String cuenta) throws ErrorServiceException {
        try {
            return usuarioRepository.findByCuentaAndEliminadoFalse(cuenta);
        } catch (Exception e) {
            throw new ErrorServiceException("No fue posible recuperar el usuario", e);
        }
    }

    @Transactional
    public Usuario cambiarRol(Long usuarioId, Rol nuevoRol) throws ErrorServiceException {
        try {
            if (nuevoRol == null) {
                throw new ErrorServiceException("Debe indicar un rol válido");
            }
            Usuario usuario = usuarioRepository.findById(usuarioId)
                    .filter(u -> !Boolean.TRUE.equals(u.isEliminado()))
                    .orElseThrow(() -> new ErrorServiceException("El usuario indicado no existe"));
            usuario.setRol(nuevoRol);
            return usuarioRepository.save(usuario);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("No fue posible actualizar el rol del usuario", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String cuenta) throws UsernameNotFoundException {
        Usuario usuario;
        try {
            usuario = buscarPorCuenta(cuenta)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        } catch (ErrorServiceException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = requestAttributes.getRequest().getSession(true);
        session.setAttribute("usuariosession", usuario);

        return new User(usuario.getCuenta(), usuario.getClave(), authorities);
    }
}
