package com.fioritech.demo.bussines.logic.service;

import com.fioritech.demo.bussines.domain.Usuario;
import com.fioritech.demo.bussines.logic.exception.BusinessException;
import com.fioritech.demo.bussines.logic.util.ValidationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UsuarioService {

    private final PersonaService personaService;

    @PersistenceContext
    private EntityManager entityManager;

    public UsuarioService(PersonaService personaService) {
        this.personaService = personaService;
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
        usuario.setEliminado(false);
        entityManager.persist(usuario);
        return usuario;
    }

    public Usuario modificarUsuario(Long id, Usuario cambios) {
        Usuario existente = entityManager.find(Usuario.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe el usuario con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("El usuario con id " + id + " esta eliminado");
        }
        verificarAtributos(cambios);
        existente.setNombre(cambios.getNombre().trim());
        existente.setApellido(cambios.getApellido().trim());
        existente.setTelefono(cambios.getTelefono().trim());
        existente.setCorreo(cambios.getCorreo().trim());
        existente.setCuenta(cambios.getCuenta().trim());
        existente.setClave(cambios.getClave());
        return entityManager.merge(existente);
    }

    public void eliminarUsuario(Long id) {
        Usuario existente = entityManager.find(Usuario.class, id);
        if (existente == null) {
            throw new EntityNotFoundException("No existe el usuario con id " + id);
        }
        if (existente.isEliminado()) {
            throw new BusinessException("El usuario con id " + id + " ya esta eliminado");
        }
        existente.setEliminado(true);
        entityManager.merge(existente);
    }

    public void verificarAtributos(Usuario usuario) {
        personaService.verificarAtributos(usuario);
        if (ValidationUtils.isBlank(usuario.getCuenta())) {
            throw new BusinessException("La cuenta es obligatoria");
        }
        if (ValidationUtils.isBlank(usuario.getClave())) {
            throw new BusinessException("La clave es obligatoria");
        }
    }
}
