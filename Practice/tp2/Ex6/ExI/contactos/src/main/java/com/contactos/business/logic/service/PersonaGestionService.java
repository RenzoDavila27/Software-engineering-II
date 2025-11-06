package com.contactos.business.logic.service;

import com.contactos.business.domain.Contacto;
import com.contactos.business.domain.ContactoCorreoElectronico;
import com.contactos.business.domain.ContactoTelefonico;
import com.contactos.business.domain.Empresa;
import com.contactos.business.domain.Persona;
import com.contactos.business.domain.Usuario;
import com.contactos.business.domain.enumeration.TipoContacto;
import com.contactos.business.logic.error.ErrorServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PersonaGestionService {

    private final PersonaService personaService;
    private final UsuarioService usuarioService;
    private final EmpresaService empresaService;

    public PersonaGestionService(PersonaService personaService,
                                 UsuarioService usuarioService,
                                 EmpresaService empresaService) {
        this.personaService = personaService;
        this.usuarioService = usuarioService;
        this.empresaService = empresaService;
    }

    @Transactional
    public Persona crearPersonaConUsuario(Persona persona,
                                          Usuario usuario,
                                          Set<Long> empresasSeleccionadas) throws ErrorServiceException {
        prepararContactosParaPersistir(persona);
        Persona creada = personaService.alta(persona);
        if (usuario != null) {
            usuario.setPersona(creada);
            usuarioService.alta(usuario);
        }
        asignarEmpresas(creada, empresasSeleccionadas);
        return creada;
    }

    @Transactional
    public Persona actualizarPersonaConUsuario(Persona persona,
                                               Usuario usuario,
                                               Set<Long> contactosEliminar,
                                               Set<Long> empresasSeleccionadas) throws ErrorServiceException {
        Persona existente = personaService.obtenerConRelaciones(persona.getId())
                .orElseThrow(() -> new ErrorServiceException("La persona indicada no existe"));

        existente.setNombre(persona.getNombre());
        existente.setApellido(persona.getApellido());

        sincronizarContactos(existente, persona.getContactos(), contactosEliminar);
        actualizarEmpresas(existente, empresasSeleccionadas);

        Persona actualizada = personaService.guardarCambios(existente);

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

    private void prepararContactosParaPersistir(Persona persona) {
        if (persona.getContactos() == null) {
            persona.setContactos(new HashSet<>());
            return;
        }
        persona.getContactos().forEach(contacto -> {
            contacto.setPersona(persona);
            contacto.setEliminado(Boolean.FALSE);
            if (contacto.getTipoContacto() == null) {
                contacto.setTipoContacto(TipoContacto.PERSONAL);
            }
        });
    }

    private void sincronizarContactos(Persona existente,
                                      Set<Contacto> nuevosContactos,
                                      Set<Long> contactosEliminar) throws ErrorServiceException {
        if (existente.getContactos() == null) {
            existente.setContactos(new HashSet<>());
        }

        Map<Long, Contacto> existentesPorId = existente.getContactos().stream()
                .filter(contacto -> contacto.getId() != null)
                .collect(Collectors.toMap(Contacto::getId, contacto -> contacto));

        if (contactosEliminar != null) {
            for (Long contactoId : contactosEliminar) {
                Contacto contacto = existentesPorId.get(contactoId);
                if (contacto == null) {
                    throw new ErrorServiceException("El contacto indicado no existe");
                }
                contacto.setEliminado(true);
            }
        }

        if (nuevosContactos == null || nuevosContactos.isEmpty()) {
            return;
        }

        for (Contacto contactoNuevo : nuevosContactos) {
            if (contactoNuevo == null) {
                continue;
            }

            if (contactoNuevo.getId() == null) {
                contactoNuevo.setPersona(existente);
                contactoNuevo.setEliminado(false);
                existente.getContactos().add(contactoNuevo);
            } else {
                Contacto contactoExistente = existentesPorId.get(contactoNuevo.getId());
                if (contactoExistente == null) {
                    throw new ErrorServiceException("El contacto indicado no existe");
                }
                contactoExistente.setEliminado(false);
                if (contactoExistente instanceof ContactoCorreoElectronico correoExistente
                        && contactoNuevo instanceof ContactoCorreoElectronico correoNuevo) {
                    correoExistente.setEmail(correoNuevo.getEmail());
                } else if (contactoExistente instanceof ContactoTelefonico telefonoExistente
                        && contactoNuevo instanceof ContactoTelefonico telefonoNuevo) {
                    telefonoExistente.setTelefono(telefonoNuevo.getTelefono());
                    telefonoExistente.setTipoTelefono(telefonoNuevo.getTipoTelefono());
                } else {
                    throw new ErrorServiceException("El tipo de contacto indicado no es válido");
                }
            }
        }
    }

    private void asignarEmpresas(Persona persona, Set<Long> empresasSeleccionadas) throws ErrorServiceException {
        if (empresasSeleccionadas == null || empresasSeleccionadas.isEmpty()) {
            persona.setEmpresas(new HashSet<>());
            return;
        }
        for (Long empresaId : empresasSeleccionadas) {
            empresaService.asignarPersona(empresaId, persona);
        }
        persona.setEmpresas(new HashSet<>(empresaService.buscarPorPersona(persona)));
    }

    private void actualizarEmpresas(Persona persona, Set<Long> empresasSeleccionadas) throws ErrorServiceException {
        if (persona.getEmpresas() == null) {
            persona.setEmpresas(new HashSet<>());
        }
        Set<Long> actuales = persona.getEmpresas().stream()
                .filter(empresa -> !Boolean.TRUE.equals(empresa.isEliminado()))
                .map(Empresa::getId)
                .collect(Collectors.toSet());

        Set<Long> seleccionadas = empresasSeleccionadas != null
                ? new HashSet<>(empresasSeleccionadas)
                : new HashSet<>();

        Set<Long> aQuitar = new HashSet<>(actuales);
        aQuitar.removeAll(seleccionadas);

        Set<Long> aAgregar = new HashSet<>(seleccionadas);
        aAgregar.removeAll(actuales);

        for (Long empresaId : aQuitar) {
            empresaService.desasignarPersona(empresaId);
        }
        for (Long empresaId : aAgregar) {
            empresaService.asignarPersona(empresaId, persona);
        }

        persona.setEmpresas(new HashSet<>(empresaService.buscarPorPersona(persona)));
    }
}
