package com.books.demo.bussiness.logic;

import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.logic.service.BaseService;
import com.books.demo.client.dto.PersonaDto;
import com.books.demo.client.exception.ApiClientException;
import com.books.demo.repository.PersonaRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PersonaService implements BaseService<PersonaDto> {

    private final PersonaRepository personaRepository;

    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @Override
    public PersonaDto alta(PersonaDto persona) throws ErrorServiceException {
        try {
            validarPersona(persona);
        } catch (IllegalArgumentException e) {
            throw new ErrorServiceException(e.getMessage(), e);
        }
        persona.setId(null);
        persona.setEliminado(false);
        try {
            return personaRepository.save(persona);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo registrar la persona.", e);
        }
    }

    @Override
    public Optional<PersonaDto> modificar(Long id, PersonaDto persona) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id de la persona es obligatorio.");
        }
        try {
            validarPersona(persona);
        } catch (IllegalArgumentException e) {
            throw new ErrorServiceException(e.getMessage(), e);
        }
        persona.setId(id);
        try {
            return personaRepository.update(id, persona);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo actualizar la persona con id " + id + ".", e);
        }
    }

    @Override
    public void baja(Long id) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id de la persona es obligatorio.");
        }
        try {
            personaRepository.deleteById(id);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo eliminar la persona con id " + id + ".", e);
        }
    }

    @Override
    public Optional<PersonaDto> obtener(Long id) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id de la persona es obligatorio.");
        }
        try {
            return personaRepository.findById(id)
                    .filter(this::personaValida);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("Error al consultar la persona con id " + id + ".", e);
        }
    }

    @Override
    public PersonaDto obtenerEntidad(Long id) throws ErrorServiceException {
        return obtener(id)
                .orElseThrow(() -> new ErrorServiceException("La persona con id " + id + " no existe."));
    }

    @Override
    public List<PersonaDto> listarActivos() throws ErrorServiceException {
        try {
            return personaRepository.findAll().stream()
                    .filter(this::personaValida)
                    .sorted(Comparator.comparing(PersonaDto::getNombre, String.CASE_INSENSITIVE_ORDER)
                            .thenComparing(persona -> persona.getApellido() == null ? "" : persona.getApellido(),
                                    String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo obtener el listado de personas.", e);
        }
    }

    public List<PersonaDto> listarPersonas() {
        try {
            return listarActivos();
        } catch (ErrorServiceException e) {
            throw asApiClientException("No se pudo obtener el listado de personas.", e);
        }
    }

    public Optional<PersonaDto> obtenerPersona(Long id) {
        try {
            return obtener(id);
        } catch (ErrorServiceException e) {
            throw asApiClientException("No se pudo obtener la persona con id " + id + ".", e);
        }
    }

    public PersonaDto crearPersona(PersonaDto persona) {
        try {
            return alta(persona);
        } catch (ErrorServiceException e) {
            throw propagate("No se pudo registrar la persona.", e);
        }
    }

    public PersonaDto actualizarPersona(Long id, PersonaDto persona) {
        try {
            return modificar(id, persona)
                    .orElseThrow(() -> new ApiClientException(
                            "La API no devolvio datos al actualizar la persona con id " + id + "."));
        } catch (ErrorServiceException e) {
            throw propagate("No se pudo actualizar la persona.", e);
        }
    }

    public void eliminarPersona(Long id) {
        try {
            baja(id);
        } catch (ErrorServiceException e) {
            throw propagate("No se pudo eliminar la persona.", e);
        }
    }

    private boolean personaValida(PersonaDto persona) {
        return persona != null && StringUtils.hasText(persona.getNombre());
    }

    private void validarPersona(PersonaDto persona) {
        if (persona == null) {
            throw new IllegalArgumentException("Los datos de la persona no pueden ser nulos.");
        }
        if (!StringUtils.hasText(persona.getNombre())) {
            throw new IllegalArgumentException("El nombre de la persona es obligatorio.");
        }
        if (!StringUtils.hasText(persona.getApellido())) {
            throw new IllegalArgumentException("El apellido de la persona es obligatorio.");
        }
        if (persona.getDni() != null && persona.getDni() <= 0) {
            throw new IllegalArgumentException("El DNI debe ser un numero positivo.");
        }
    }

    private RuntimeException propagate(String defaultMessage, ErrorServiceException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof IllegalArgumentException illegalArgumentException) {
            return illegalArgumentException;
        }
        if (cause instanceof ApiClientException apiClientException) {
            return apiClientException;
        }
        return new ApiClientException(defaultMessage, exception);
    }

    private ApiClientException asApiClientException(String message, ErrorServiceException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof ApiClientException apiClientException) {
            return apiClientException;
        }
        return new ApiClientException(message, exception);
    }
}
