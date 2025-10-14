package com.books.demo.bussiness.logic;

import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.logic.service.BaseService;
import com.books.demo.client.dto.LocalidadDto;
import com.books.demo.client.exception.ApiClientException;
import com.books.demo.repository.LocalidadRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LocalidadService implements BaseService<LocalidadDto> {

    private final LocalidadRepository localidadRepository;

    public LocalidadService(LocalidadRepository localidadRepository) {
        this.localidadRepository = localidadRepository;
    }

    @Override
    public LocalidadDto alta(LocalidadDto localidad) throws ErrorServiceException {
        try {
            validarLocalidad(localidad);
        } catch (IllegalArgumentException e) {
            throw new ErrorServiceException(e.getMessage(), e);
        }
        localidad.setId(null);
        localidad.setEliminado(false);
        try {
            return localidadRepository.save(localidad);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo registrar la localidad.", e);
        }
    }

    @Override
    public Optional<LocalidadDto> modificar(Long id, LocalidadDto localidad) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id de la localidad es obligatorio.");
        }
        try {
            validarLocalidad(localidad);
        } catch (IllegalArgumentException e) {
            throw new ErrorServiceException(e.getMessage(), e);
        }
        localidad.setId(id);
        try {
            return localidadRepository.update(id, localidad);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo actualizar la localidad con id " + id + ".", e);
        }
    }

    @Override
    public void baja(Long id) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id de la localidad es obligatorio.");
        }
        try {
            localidadRepository.deleteById(id);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo eliminar la localidad con id " + id + ".", e);
        }
    }

    @Override
    public Optional<LocalidadDto> obtener(Long id) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id de la localidad es obligatorio.");
        }
        try {
            return localidadRepository.findById(id)
                    .filter(this::localidadValida);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("Error al consultar la localidad con id " + id + ".", e);
        }
    }

    @Override
    public LocalidadDto obtenerEntidad(Long id) throws ErrorServiceException {
        return obtener(id)
                .orElseThrow(() -> new ErrorServiceException("La localidad con id " + id + " no existe."));
    }

    @Override
    public List<LocalidadDto> listarActivos() throws ErrorServiceException {
        try {
            return localidadRepository.findAll().stream()
                    .filter(this::localidadValida)
                    .sorted(Comparator.comparing(LocalidadDto::getDenominacion, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo obtener el listado de localidades.", e);
        }
    }

    public List<LocalidadDto> listarLocalidades() {
        try {
            return listarActivos();
        } catch (ErrorServiceException e) {
            throw asApiClientException("No se pudo obtener el listado de localidades.", e);
        }
    }

    public Optional<LocalidadDto> obtenerLocalidad(Long id) {
        try {
            return obtener(id);
        } catch (ErrorServiceException e) {
            throw asApiClientException("No se pudo obtener la localidad con id " + id + ".", e);
        }
    }

    public LocalidadDto crearLocalidad(LocalidadDto localidad) {
        try {
            return alta(localidad);
        } catch (ErrorServiceException e) {
            throw propagate("No se pudo registrar la localidad.", e);
        }
    }

    public LocalidadDto actualizarLocalidad(Long id, LocalidadDto localidad) {
        try {
            return modificar(id, localidad)
                    .orElseThrow(() -> new ApiClientException(
                            "La API no devolvio datos al actualizar la localidad con id " + id + "."));
        } catch (ErrorServiceException e) {
            throw propagate("No se pudo actualizar la localidad.", e);
        }
    }

    public void eliminarLocalidad(Long id) {
        try {
            baja(id);
        } catch (ErrorServiceException e) {
            throw propagate("No se pudo eliminar la localidad.", e);
        }
    }

    private boolean localidadValida(LocalidadDto localidad) {
        return localidad != null && StringUtils.hasText(localidad.getDenominacion());
    }

    private void validarLocalidad(LocalidadDto localidad) {
        if (localidad == null) {
            throw new IllegalArgumentException("Los datos de la localidad no pueden ser nulos.");
        }
        if (!StringUtils.hasText(localidad.getDenominacion())) {
            throw new IllegalArgumentException("La denominacion de la localidad es obligatoria.");
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
