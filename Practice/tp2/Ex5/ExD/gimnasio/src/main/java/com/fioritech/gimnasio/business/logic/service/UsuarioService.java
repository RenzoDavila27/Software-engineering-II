package com.fioritech.gimnasio.business.logic.service;

import com.fioritech.gimnasio.business.domain.Usuario;
import com.fioritech.gimnasio.business.domain.enums.RolUsuario;
import com.fioritech.gimnasio.business.logic.error.BusinessException;
import com.fioritech.gimnasio.business.persistence.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Usuario crearUsuario(String nombreUsuario, String clave, RolUsuario rol) {
        validar(nombreUsuario, clave, rol);
        validarNombreUnico(nombreUsuario, null);
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombreUsuario.trim());
        usuario.setClave(passwordEncoder.encode(clave));
        usuario.setRol(rol);
        return usuarioRepository.save(usuario);
    }

    public void validar(String nombreUsuario, String clave, RolUsuario rol) {
        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new BusinessException("El nombre de usuario es obligatorio");
        }
        if (clave == null || clave.length() < 6) {
            throw new BusinessException("La clave debe tener al menos 6 caracteres");
        }
        if (rol == null) {
            throw new BusinessException("El rol es obligatorio");
        }
    }

    private void validarNombreUnico(String nombreUsuario, String idActual) {
        String normalized = nombreUsuario.trim().toLowerCase(Locale.ROOT);
        boolean existe = usuarioRepository.findAll().stream()
            .filter(u -> !u.isEliminado())
            .filter(u -> idActual == null || !u.getId().equals(idActual))
            .anyMatch(u -> u.getNombreUsuario().trim().toLowerCase(Locale.ROOT).equals(normalized));
        if (existe) {
            throw new BusinessException("Ya existe un usuario con ese nombre");
        }
    }

    public Usuario modificarUsuario(String id, String nombreUsuario, String clave, RolUsuario rol) {
        Usuario usuario = buscarUsuario(id);
        if (nombreUsuario != null && !nombreUsuario.isBlank()
            && !usuario.getNombreUsuario().equalsIgnoreCase(nombreUsuario.trim())) {
            validarNombreUnico(nombreUsuario, usuario.getId());
            usuario.setNombreUsuario(nombreUsuario.trim());
        }
        if (clave != null && !clave.isBlank()) {
            if (clave.length() < 6) {
                throw new BusinessException("La clave debe tener al menos 6 caracteres");
            }
            usuario.setClave(passwordEncoder.encode(clave));
        }
        if (rol != null) {
            usuario.setRol(rol);
        }
        return usuarioRepository.save(usuario);
    }

    public void eliminarUsuario(String id) {
        Usuario usuario = buscarUsuario(id);
        usuario.setEliminado(true);
        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuario(String id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException("Debe indicar el usuario");
        }

        try {
            Usuario usuario = buscarUsuarioPorNombre(username);
            String rol = usuario.getRol() != null ? usuario.getRol().name() : "USUARIO";

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol));

            try {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
                HttpSession session = attributes.getRequest().getSession(true);
                session.setAttribute("usuarioSession", usuario);
                session.setAttribute("rol", rol);
            } catch (IllegalStateException ignored) {
                // No hay request activa (por ejemplo, tareas en segundo plano)
            }

            return new User(usuario.getNombreUsuario(), usuario.getClave(), authorities);

        } catch (BusinessException e) {
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuario() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarioActivo() {
        return usuarioRepository.findAll().stream()
            .filter(u -> !u.isEliminado())
            .toList();
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorNombre(String nombreUsuario) {
        String normalized = nombreUsuario == null ? "" : nombreUsuario.trim().toLowerCase(Locale.ROOT);
        return usuarioRepository.findAll().stream()
            .filter(u -> !u.isEliminado())
            .filter(u -> u.getNombreUsuario().trim().toLowerCase(Locale.ROOT).equals(normalized))
            .findFirst()
            .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
    }


    public Usuario modificarClave(String id, String claveActual, String nuevaClave, String confirmarClave) {
        Usuario usuario = buscarUsuario(id);
        if (!passwordEncoder.matches(claveActual, usuario.getClave())) {
            throw new BusinessException("La clave actual es incorrecta");
        }
        if (nuevaClave == null || nuevaClave.length() < 6) {
            throw new BusinessException("La nueva clave debe tener al menos 6 caracteres");
        }
        if (!nuevaClave.equals(confirmarClave)) {
            throw new BusinessException("La nueva clave y su confirmacion no coinciden");
        }
        usuario.setClave(passwordEncoder.encode(nuevaClave));
        return usuarioRepository.save(usuario);
    }

    public Collection<Usuario> listarUsuariosPorTipo(RolUsuario tipo){
        return usuarioRepository.listarUsuariosPorTipo(tipo);
    }

    public Usuario login(String cuenta, String clave){
        try {

            if (cuenta == null || cuenta.trim().isEmpty()) {
                throw new BusinessException("Debe indicar la cuenta");
            }

            if (clave == null || clave.trim().isEmpty()) {
                throw new BusinessException("Debe indicar la clave");
            }

            Usuario usuario = buscarUsuarioPorNombre(cuenta);
            if (!passwordEncoder.matches(clave, usuario.getClave())) {
                throw new BusinessException("No existe usuario para la cuenta o clave indicada");
            }

            return usuario;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("Error de Sistemas");
        }
    }
}
