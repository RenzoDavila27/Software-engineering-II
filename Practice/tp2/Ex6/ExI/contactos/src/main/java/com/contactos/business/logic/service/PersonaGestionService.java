package com.contactos.business.logic.service;

import com.contactos.business.domain.Persona;
import com.contactos.business.domain.Usuario;
import com.contactos.business.logic.error.ErrorServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonaGestionService {

    private final PersonaService personaService;
    private final UsuarioService usuarioService;

    public PersonaGestionService(PersonaService personaService,
                                 UsuarioService usuarioService) {
        this.personaService = personaService;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public Persona crearPersonaConUsuario(Persona persona, Usuario usuario) throws ErrorServiceException {
        Persona creada = personaService.alta(persona);
        if (usuario != null) {
            usuario.setPersona(creada);
            usuarioService.alta(usuario);
        }
        return creada;
    }

    @Transactional
    public Persona actualizarPersonaConUsuario(Persona persona, Usuario usuario) throws ErrorServiceException {
        Persona actualizada = personaService.modificar(persona.getId(), persona)
                .orElseThrow(() -> new ErrorServiceException("La persona indicada no existe"));

        if (usuario != null) {
            usuario.setPersona(actualizada);
            if (usuario.getId() == null) {
                usuarioService.alta(usuario);
            } else {
                usuarioService.modificar(usuario.getId(), usuario)
                        .orElseThrow(() -> new ErrorServiceException("El usuario indicado no existe"));
            }
        }
        return actualizada;
    }

    @Transactional
    public void eliminarPersonaConUsuarios(Long personaId) throws ErrorServiceException {
        Persona persona = personaService.obtenerConRelaciones(personaId)
                .orElseThrow(() -> new ErrorServiceException("La persona indicada no existe o ya está eliminada"));

        personaService.baja(personaId);
        if (persona.getUsuarios() != null) {
            for (Usuario usuario : persona.getUsuarios()) {
                if (Boolean.TRUE.equals(usuario.isEliminado())) {
                    continue;
                }
                usuarioService.baja(usuario.getId());
            }
        }
    }
}
