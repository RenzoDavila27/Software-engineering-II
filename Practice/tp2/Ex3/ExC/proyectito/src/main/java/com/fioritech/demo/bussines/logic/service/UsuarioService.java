package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Usuario;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.service.template.CrudTemplateService;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import com.fioritech.demo.bussines.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService extends CrudTemplateService<Usuario, Long> {

    private final PersonaService personaService;
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(PersonaService personaService, UsuarioRepository usuarioRepository) {
        this.personaService = personaService;
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario crearUsuario(Usuario usuario) {
        return crearEntidad(usuario);
    }

    public Usuario modificarUsuario(Long id, Usuario cambios) {
        return modificarEntidad(id, cambios);
    }

    public void eliminarUsuario(Long id) {
        eliminarEntidad(id);
    }

    @Transactional(readOnly = true)
    public Collection<Usuario> listarUsuarios() {
        return listarEntidades();
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuarioPorId(Long id) {
        return buscarEntidad(id);
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

    public Usuario login(String cuenta, String clave) {
        if (ValidationUtils.isBlank(cuenta)) {
            throw new BusinessException("Debe indicar la cuenta");
        }
        if (ValidationUtils.isBlank(clave)) {
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
    }

    public void modificarClave(String cuenta, String clave, String clavenueva) {
        Usuario usuario = usuarioRepository.findByCuenta(cuenta)
                .orElseThrow(() -> new BusinessException("No existe la cuenta indicada"));
        if (!usuario.getClave().equals(clave)) {
            throw new BusinessException("La clave actual no coincide");
        }
        usuario.setClave(clavenueva);
        usuarioRepository.save(usuario);
    }

    @Override
    protected void validarEntidad(Usuario usuario) {
        verificarAtributos(usuario);
    }

    @Override
    protected void validarEntidadNueva(Usuario usuario) {
        if (usuario.getId() != null) {
            throw new BusinessException("El usuario ya tiene un id asignado");
        }
    }

    @Override
    protected void antesDeCrear(Usuario usuario) {
        normalizar(usuario);
        usuario.setEliminado(false);
    }

    @Override
    protected void aplicarCambios(Usuario existente, Usuario cambios) {
        normalizar(cambios);
        existente.setNombre(cambios.getNombre());
        existente.setApellido(cambios.getApellido());
        existente.setTelefono(cambios.getTelefono());
        existente.setCorreo(cambios.getCorreo());
        existente.setCuenta(cambios.getCuenta());
        existente.setClave(cambios.getClave());
    }

    @Override
    protected void marcarEliminado(Usuario usuario) {
        usuario.setEliminado(true);
    }

    @Override
    protected Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    protected Usuario obtenerPorId(Long id) {
        return obtenerUsuarioActivo(id);
    }

    @Override
    protected Collection<Usuario> obtenerListado() {
        return usuarioRepository.buscarUsuariosActivos();
    }

    private void normalizar(Usuario usuario) {
        usuario.setNombre(usuario.getNombre().trim());
        usuario.setApellido(usuario.getApellido().trim());
        usuario.setTelefono(usuario.getTelefono().trim());
        usuario.setCorreo(usuario.getCorreo().trim());
        usuario.setCuenta(usuario.getCuenta().trim());
        usuario.setClave(usuario.getClave().trim());
    }

    private Usuario obtenerUsuarioActivo(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No existe el usuario con id " + id));
        if (usuario.isEliminado()) {
            throw new BusinessException("El usuario con id " + id + " esta eliminado");
        }
        return usuario;
    }
}

