package com.books.demo.bussiness.logic.service;

import com.books.demo.bussiness.domain.Domicilio;
import com.books.demo.bussiness.domain.Persona;
import com.books.demo.bussiness.persistance.PersonaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonaService {
    @Autowired
    private final PersonaRepository personaRepository;
    @Autowired
    private final DomicilioService domicilioService;

    public PersonaService(PersonaRepository personaRepository, DomicilioService domicilioService) {
        this.personaRepository = personaRepository;
        this.domicilioService = domicilioService;
    }

    @Transactional
    public Persona crearPersona(Persona persona) {
        try {
            validarPersona(persona);
            persona.setDomicilio(obtenerDomicilio(persona.getDomicilio()));
            persona.setEliminado(false);
            return personaRepository.save(persona);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear persona", e);
        }
    }

    @Transactional
    public Persona modificarPersona(Long id, Persona datosActualizados) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id de la persona no puede ser nulo");
            }
            validarPersona(datosActualizados);
            Persona persona = personaRepository.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada con id " + id));
            persona.setNombre(datosActualizados.getNombre());
            persona.setApellido(datosActualizados.getApellido());
            persona.setDni(datosActualizados.getDni());
            persona.setDomicilio(obtenerDomicilio(datosActualizados.getDomicilio()));
            return personaRepository.save(persona);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al modificar persona", e);
        }
    }

    @Transactional
    public void eliminarPersona(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id de la persona no puede ser nulo");
            }
            Persona persona = personaRepository.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada con id " + id));
            persona.setEliminado(true);
            personaRepository.save(persona);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar persona", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Persona> listarActivas() {
        try {
            return personaRepository.listarPersonasActivas();
        } catch (Exception e) {
            throw new RuntimeException("Error al listar personas", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Persona> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id de la persona no puede ser nulo");
        }
        try {
            return personaRepository.buscarPorId(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar persona", e);
        }
    }

    private void validarPersona(Persona persona) {
        if (persona == null) {
            throw new IllegalArgumentException("La persona no puede ser nula");
        }
        if (textoInvalido(persona.getNombre())) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (textoInvalido(persona.getApellido())) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }
        if (persona.getDni() == null) {
            throw new IllegalArgumentException("El DNI es obligatorio");
        }
        if (persona.getDomicilio() == null || persona.getDomicilio().getId() == null) {
            throw new IllegalArgumentException("El domicilio es obligatorio y debe existir previamente");
        }
    }

    private Domicilio obtenerDomicilio(Domicilio domicilio) {
        return domicilioService.buscarPorId(domicilio.getId())
                .orElseThrow(() -> new IllegalArgumentException("Domicilio no encontrado con id " + domicilio.getId()));
    }

    private boolean textoInvalido(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
