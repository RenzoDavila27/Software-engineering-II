package com.contactos.business.logic.service;

import com.contactos.business.domain.Contacto;
import com.contactos.business.domain.ContactoCorreoElectronico;
import com.contactos.business.domain.ContactoTelefonico;
import com.contactos.business.domain.Empresa;
import com.contactos.business.domain.Persona;
import com.contactos.business.domain.enumeration.TipoContacto;
import com.contactos.business.logic.error.ErrorServiceException;
import com.contactos.business.persistence.repository.EmpresaRepository;
import com.contactos.business.persistence.repository.PersonaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        if (!StringUtils.hasText(entidad.getNombre())) {
            throw new ErrorServiceException("El nombre de la empresa es requerido");
        }
        if (useCase == BaseUseCaseService.ALTA &&
                (entidad.getContactos() == null || entidad.getContactos().isEmpty())) {
            throw new ErrorServiceException("Debe indicar al menos un contacto para la empresa");
        }
    }

    @Override
    protected void preAlta(Empresa entidad) throws ErrorServiceException {
        prepararPersona(entidad);
        prepararContactos(entidad);
    }

    @Override
    protected void preModificacion(Empresa entidad) throws ErrorServiceException {
        prepararPersona(entidad);
        prepararContactos(entidad);
    }

    @Override
    @Transactional
    public Empresa alta(Empresa entidad) throws ErrorServiceException {
        prepararPersona(entidad);
        prepararContactos(entidad);
        return super.alta(entidad);
    }

    @Override
    @Transactional
    public Optional<Empresa> modificar(Long id, Empresa entidadNueva) throws ErrorServiceException {
        prepararPersona(entidadNueva);
        prepararContactos(entidadNueva);
        return super.modificar(id, entidadNueva);
    }

    @Transactional(readOnly = true)
    public Empresa obtenerConContactos(Long id) throws ErrorServiceException {
        try {
            return empresaRepository.findWithContactosById(id)
                    .filter(empresa -> !Boolean.TRUE.equals(empresa.isEliminado()))
                    .orElseThrow(() -> new ErrorServiceException("La empresa indicada no existe"));
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("No fue posible recuperar la empresa solicitada", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Empresa> listarActivasConContactos() throws ErrorServiceException {
        try {
            return empresaRepository.findAll().stream()
                    .filter(empresa -> !Boolean.TRUE.equals(empresa.isEliminado()))
                    .map(empresa -> empresaRepository.findWithContactosById(empresa.getId()).orElse(empresa))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ErrorServiceException("No fue posible listar las empresas", e);
        }
    }

    @Transactional
    public Empresa crearEmpresa(Empresa empresa) throws ErrorServiceException {
        try {
            prepararPersona(empresa);
            prepararContactos(empresa);
            if (empresa.getContactos() == null || empresa.getContactos().isEmpty()) {
                throw new ErrorServiceException("Debe indicar al menos un contacto para la empresa");
            }
            return super.alta(empresa);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("No fue posible registrar la empresa", e);
        }
    }

    @Transactional
    public Empresa actualizarEmpresa(Long id,
                                     Empresa empresaActualizada,
                                     Set<Long> contactosEliminar) throws ErrorServiceException {
        try {
            Empresa existente = obtenerConContactos(id);
            actualizarCamposBasicos(existente, empresaActualizada);
            sincronizarContactos(existente, empresaActualizada.getContactos(), contactosEliminar);
            return empresaRepository.save(existente);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("No fue posible actualizar la empresa", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Empresa> buscarPorPersona(Persona persona) throws ErrorServiceException {
        try {
            return empresaRepository.findByPersonaAndEliminadoFalse(persona);
        } catch (Exception e) {
            throw new ErrorServiceException("No fue posible recuperar las empresas de la persona", e);
        }
    }

    private void prepararPersona(Empresa entidad) throws ErrorServiceException {
        if (entidad.getPersona() != null && entidad.getPersona().getId() != null) {
            Persona persona = personaRepository.findById(entidad.getPersona().getId())
                    .filter(p -> !Boolean.TRUE.equals(p.isEliminado()))
                    .orElseThrow(() -> new ErrorServiceException("La persona asociada no existe o está eliminada"));
            entidad.setPersona(persona);
        } else {
            entidad.setPersona(null);
        }
    }

    private void prepararContactos(Empresa entidad) throws ErrorServiceException {
        if (entidad.getContactos() == null) {
            entidad.setContactos(new HashSet<>());
            return;
        }
        entidad.getContactos().removeIf(Objects::isNull);
        for (Contacto contacto : entidad.getContactos()) {
            contacto.setEmpresa(entidad);
            contacto.setPersona(null);
            contacto.setTipoContacto(TipoContacto.LABORAL);
            contacto.setEliminado(Boolean.FALSE);
        }
    }

    private void actualizarCamposBasicos(Empresa existente, Empresa actualizada) throws ErrorServiceException {
        existente.setNombre(actualizada.getNombre());
        if (actualizada.getPersona() != null && actualizada.getPersona().getId() != null) {
            Persona persona = personaRepository.findById(actualizada.getPersona().getId())
                    .filter(p -> !Boolean.TRUE.equals(p.isEliminado()))
                    .orElseThrow(() -> new ErrorServiceException("La persona asociada no existe o está eliminada"));
            existente.setPersona(persona);
        } else {
            existente.setPersona(null);
        }
    }

    private void sincronizarContactos(Empresa existente,
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
                if (contacto != null) {
                    contacto.setEliminado(Boolean.TRUE);
                }
            }
        }

        if (nuevosContactos == null) {
            return;
        }

        for (Contacto contactoNuevo : nuevosContactos) {
            if (contactoNuevo == null) {
                continue;
            }
            if (contactoNuevo.getId() == null) {
                prepararContactoNuevo(existente, contactoNuevo);
                existente.getContactos().add(contactoNuevo);
            } else {
                Contacto contactoExistente = existentesPorId.get(contactoNuevo.getId());
                if (contactoExistente == null) {
                    throw new ErrorServiceException("El contacto indicado no existe");
                }
                contactoExistente.setEliminado(Boolean.FALSE);
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

        boolean hayContactosActivos = existente.getContactos().stream()
                .anyMatch(contacto -> !Boolean.TRUE.equals(contacto.isEliminado()));
        if (!hayContactosActivos) {
            throw new ErrorServiceException("La empresa debe tener al menos un contacto activo");
        }
    }

    private void prepararContactoNuevo(Empresa empresa, Contacto contacto) {
        contacto.setEmpresa(empresa);
        contacto.setPersona(null);
        contacto.setTipoContacto(TipoContacto.LABORAL);
        contacto.setEliminado(Boolean.FALSE);
    }

    @Transactional
    public void asignarPersona(Long empresaId, Persona persona) throws ErrorServiceException {
        try {
            Empresa empresa = obtenerConContactos(empresaId);
            if (persona != null && persona.getId() != null) {
                Persona personaPersistida = personaRepository.findById(persona.getId())
                        .filter(p -> !Boolean.TRUE.equals(p.isEliminado()))
                        .orElseThrow(() -> new ErrorServiceException("La persona indicada no existe"));
                empresa.setPersona(personaPersistida);
            } else {
                empresa.setPersona(null);
            }
            empresaRepository.save(empresa);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("No fue posible asignar la empresa a la persona", e);
        }
    }

    @Transactional
    public void desasignarPersona(Long empresaId) throws ErrorServiceException {
        try {
            Empresa empresa = obtenerConContactos(empresaId);
            empresa.setPersona(null);
            empresaRepository.save(empresa);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("No fue posible desasignar la empresa", e);
        }
    }
}
