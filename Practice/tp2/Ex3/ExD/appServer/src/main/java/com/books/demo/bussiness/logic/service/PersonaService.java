package com.books.demo.bussiness.logic.service;

import com.books.demo.bussiness.domain.Domicilio;
import com.books.demo.bussiness.domain.Persona;
import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.persistance.PersonaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class PersonaService extends BaseService<Persona> {

    private final PersonaRepository personaRepository;
    private final DomicilioService domicilioService;

    public PersonaService(PersonaRepository personaRepository, DomicilioService domicilioService) {
        super(personaRepository);
        this.personaRepository = personaRepository;
        this.domicilioService = domicilioService;
    }

    @Transactional
    public Persona crearPersona(Persona persona) throws ErrorServiceException {
        return alta(persona);
    }

    @Transactional
    public Persona modificarPersona(Long id, Persona datosActualizados) throws ErrorServiceException {
        return modificar(id, datosActualizados)
                .orElseThrow(() -> new ErrorServiceException("Persona no encontrada con id " + id));
    }

    @Transactional
    public void eliminarPersona(Long id) throws ErrorServiceException {
        baja(id);
    }

    @Transactional(readOnly = true)
    public List<Persona> listarActivas() throws ErrorServiceException {
        return super.listarActivos();
    }

    @Transactional(readOnly = true)
    public Optional<Persona> buscarPorId(Long id) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id de la persona no puede ser nulo.");
        }
        return obtener(id);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Persona entidad) throws ErrorServiceException {
        if (entidad == null) {
            throw new ErrorServiceException("La persona no puede ser nula.");
        }
        if (!StringUtils.hasText(entidad.getNombre())) {
            throw new ErrorServiceException("El nombre es obligatorio.");
        }
        if (!StringUtils.hasText(entidad.getApellido())) {
            throw new ErrorServiceException("El apellido es obligatorio.");
        }
        if (entidad.getDni() == null) {
            throw new ErrorServiceException("El DNI es obligatorio.");
        }
        if (entidad.getDomicilio() == null || entidad.getDomicilio().getId() == null) {
            throw new ErrorServiceException("El domicilio es obligatorio y debe existir previamente.");
        }
    }

    @Override
    protected void preAlta(Persona entidad) throws ErrorServiceException {
        entidad.setDomicilio(obtenerDomicilioPersistente(entidad.getDomicilio()));
        entidad.setEliminado(false);
    }

    @Override
    protected void preModificacion(Persona entidad) throws ErrorServiceException {
        entidad.setDomicilio(obtenerDomicilioPersistente(entidad.getDomicilio()));
    }

    private Domicilio obtenerDomicilioPersistente(Domicilio domicilio) throws ErrorServiceException {
        Long id = domicilio != null ? domicilio.getId() : null;
        if (id == null) {
            throw new ErrorServiceException("Debe indicar un domicilio válido.");
        }
        return domicilioService.buscarPorId(id)
                .orElseThrow(() -> new ErrorServiceException("Domicilio no encontrado con id " + id));
    }
}
