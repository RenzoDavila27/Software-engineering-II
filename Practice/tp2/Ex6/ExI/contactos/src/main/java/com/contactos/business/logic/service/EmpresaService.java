package com.contactos.business.logic.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.contactos.business.domain.Contacto;
import com.contactos.business.domain.Empresa;
import com.contactos.business.domain.Persona;
import com.contactos.business.domain.enumeration.TipoContacto;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.persistence.repository.EmpresaRepository;
import com.contactos.business.persistence.repository.PersonaRepository;

@Service
public class EmpresaService extends BaseService<Empresa, Long> {

    private final EmpresaRepository empresaRepository;
    private final PersonaRepository personaRepository;

    public EmpresaService(EmpresaRepository empresaRepository, PersonaRepository personaRepository) {
        super(empresaRepository);
        this.empresaRepository = empresaRepository;
        this.personaRepository = personaRepository;
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Empresa entidad) throws ErrorServiceException {
        if (entidad == null) {
            throw new ErrorServiceException("La empresa es requerida");
        }
        if (entidad.getNombre() == null || entidad.getNombre().isBlank()) {
            throw new ErrorServiceException("El nombre de la empresa es requerido");
        }
        if (entidad.getPersona() == null || entidad.getPersona().getId() == null) {
            throw new ErrorServiceException("Debe asociar la empresa a una persona válida");
        }
        if (entidad.getContacto() == null) {
            throw new ErrorServiceException("Debe asociar un contacto laboral a la empresa");
        }
    }

    @Override
    protected void preAlta(Empresa entidad) throws ErrorServiceException {
        Persona persona = obtenerPersona(entidad.getPersona().getId());
        entidad.setPersona(persona);
        prepararContacto(entidad.getContacto(), persona);
    }

    @Override
    protected void preModificacion(Empresa entidad) throws ErrorServiceException {
        Persona persona = obtenerPersona(entidad.getPersona().getId());
        entidad.setPersona(persona);
        prepararContacto(entidad.getContacto(), persona);
    }

    private Persona obtenerPersona(Long personaId) throws ErrorServiceException {
        return personaRepository.findById(personaId)
                .filter(p -> !Boolean.TRUE.equals(p.isEliminado()))
                .orElseThrow(() -> new ErrorServiceException("La persona asociada no existe o está eliminada"));
    }

    private void prepararContacto(Contacto contacto, Persona persona) throws ErrorServiceException {
        if (contacto == null) {
            throw new ErrorServiceException("El contacto asociado es requerido");
        }
        contacto.setPersona(persona);
        contacto.setTipoContacto(TipoContacto.LABORAL);
        contacto.setEliminado(false);
    }

    @Transactional(readOnly = true)
    public List<Empresa> buscarPorPersona(Persona persona) throws ErrorServiceException {
        try {
            return empresaRepository.findByPersonaAndEliminadoFalse(persona);
        } catch (Exception e) {
            throw new ErrorServiceException("No fue posible recuperar las empresas de la persona", e);
        }
    }
}
