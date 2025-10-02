package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Usuario;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import com.fioritech.demo.bussines.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    private final PersonaService personaService;
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(PersonaService personaService, UsuarioRepository usuarioRepository) throws BusinessException {
        this.personaService = personaService;
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario crearUsuario(Usuario usuario) {
        verificarAtributos(usuario);
        if (usuario.getId() != null) {
            throw new BusinessException("El usuario ya tiene un id asignado");
        }
        usuario.setNombre(usuario.getNombre().trim());
        usuario.setApellido(usuario.getApellido().trim());
        usuario.setTelefono(usuario.getTelefono().trim());
        usuario.setCorreo(usuario.getCorreo().trim());
        usuario.setCuenta(usuario.getCuenta().trim());
        usuario.setClave(usuario.getClave().trim());
        usuario.setEliminado(false);
        return usuarioRepository.save(usuario);
    }

    public Usuario modificarUsuario(Long id, Usuario cambios) {
        Usuario existente = obtenerUsuarioActivo(id);
        verificarAtributos(cambios);
        existente.setNombre(cambios.getNombre().trim());
        existente.setApellido(cambios.getApellido().trim());
        existente.setTelefono(cambios.getTelefono().trim());
        existente.setCorreo(cambios.getCorreo().trim());
        existente.setCuenta(cambios.getCuenta().trim());
        existente.setClave(cambios.getClave().trim());
        return usuarioRepository.save(existente);
    }

    public void eliminarUsuario(Long id) {
        Usuario existente = obtenerUsuarioActivo(id);
        existente.setEliminado(true);
        usuarioRepository.save(existente);
    }

    @Transactional(readOnly = true)
    public Collection<Usuario> listarUsuarios() {
        return usuarioRepository.buscarUsuariosActivos();
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorId(Long id) {
        return obtenerUsuarioActivo(id);
    }

    public void verificarAtributos(Usuario usuario) {
        personaService.verificarAtributos(usuario);
        if (ValidationUtils.isBlank(usuario.getCuenta())) {
            throw new BusinessException("El nombre de la cuenta es obligatorio");
        }
        if (ValidationUtils.isBlank(usuario.getClave())) {
            throw new BusinessException("La clave es obligatoria");
        }
    }

    private Usuario obtenerUsuarioActivo(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el usuario con id " + id));
        if (usuario.isEliminado()) {
            throw new BusinessException("El usuario con id " + id + " esta eliminado");
        }
        return usuario;
    }

    public Usuario login(String cuenta, String clave) {
        try {
            if (cuenta == null || cuenta.trim().isEmpty()) {
                throw new BusinessException("Debe indicar la cuenta");
            }

            if (clave == null || clave.trim().isEmpty()) {
                throw new BusinessException("Debe indicar la clave");
            }

            Optional<Usuario> usuarioOpt = usuarioRepository.findByCuenta(cuenta);

            if (usuarioOpt.isEmpty()) {
                throw new BusinessException("No existe usuario para la cuenta indicada");
            }

            Usuario usuario = usuarioOpt.get();

            if (usuario.isEliminado()) {
                throw new BusinessException("El usuario está eliminado");
            }

            if (!usuario.getClave().equals(clave)) {
                throw new BusinessException("La clave es incorrecta");
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
