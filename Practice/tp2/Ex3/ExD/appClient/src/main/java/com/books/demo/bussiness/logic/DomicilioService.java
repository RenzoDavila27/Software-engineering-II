package com.books.demo.bussiness.logic;

import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.logic.service.BaseService;
import com.books.demo.client.dto.DomicilioDto;
import com.books.demo.client.exception.ApiClientException;
import com.books.demo.repository.DomicilioRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DomicilioService implements BaseService<DomicilioDto> {

    private final DomicilioRepository domicilioRepository;

    public DomicilioService(DomicilioRepository domicilioRepository) {
        this.domicilioRepository = domicilioRepository;
    }

    @Override
    public DomicilioDto alta(DomicilioDto domicilio) throws ErrorServiceException {
        try {
            validarDomicilio(domicilio);
        } catch (IllegalArgumentException e) {
            throw new ErrorServiceException(e.getMessage(), e);
        }
        domicilio.setId(null);
        domicilio.setEliminado(false);
        try {
            return domicilioRepository.save(domicilio);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo registrar el domicilio.", e);
        }
    }

    @Override
    public Optional<DomicilioDto> modificar(Long id, DomicilioDto domicilio) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id del domicilio es obligatorio.");
        }
        try {
            validarDomicilio(domicilio);
        } catch (IllegalArgumentException e) {
            throw new ErrorServiceException(e.getMessage(), e);
        }
        domicilio.setId(id);
        try {
            return domicilioRepository.update(id, domicilio);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo actualizar el domicilio con id " + id + ".", e);
        }
    }

    @Override
    public void baja(Long id) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id del domicilio es obligatorio.");
        }
        try {
            domicilioRepository.deleteById(id);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo eliminar el domicilio con id " + id + ".", e);
        }
    }

    @Override
    public Optional<DomicilioDto> obtener(Long id) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id del domicilio es obligatorio.");
        }
        try {
            return domicilioRepository.findById(id)
                    .filter(this::domicilioValido);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("Error al consultar el domicilio con id " + id + ".", e);
        }
    }

    @Override
    public DomicilioDto obtenerEntidad(Long id) throws ErrorServiceException {
        return obtener(id)
                .orElseThrow(() -> new ErrorServiceException("El domicilio con id " + id + " no existe."));
    }

    @Override
    public List<DomicilioDto> listarActivos() throws ErrorServiceException {
        try {
            return domicilioRepository.findAll().stream()
                    .filter(this::domicilioValido)
                    .collect(Collectors.toList());
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo obtener el listado de domicilios.", e);
        }
    }

    public List<DomicilioDto> listarDomicilios() {
        try {
            return listarActivos();
        } catch (ErrorServiceException e) {
            throw asApiClientException("No se pudo obtener el listado de domicilios.", e);
        }
    }

    public Optional<DomicilioDto> obtenerDomicilio(Long id) {
        try {
            return obtener(id);
        } catch (ErrorServiceException e) {
            throw asApiClientException("No se pudo obtener el domicilio con id " + id + ".", e);
        }
    }

    public DomicilioDto crearDomicilio(DomicilioDto domicilio) {
        try {
            return alta(domicilio);
        } catch (ErrorServiceException e) {
            throw propagate("No se pudo registrar el domicilio.", e);
        }
    }

    public DomicilioDto actualizarDomicilio(Long id, DomicilioDto domicilio) {
        try {
            return modificar(id, domicilio)
                    .orElseThrow(() -> new ApiClientException(
                            "La API no devolvio datos al actualizar el domicilio con id " + id + "."));
        } catch (ErrorServiceException e) {
            throw propagate("No se pudo actualizar el domicilio.", e);
        }
    }

    public void eliminarDomicilio(Long id) {
        try {
            baja(id);
        } catch (ErrorServiceException e) {
            throw propagate("No se pudo eliminar el domicilio.", e);
        }
    }

    private boolean domicilioValido(DomicilioDto domicilio) {
        return domicilio != null && StringUtils.hasText(domicilio.getCalle());
    }

    private void validarDomicilio(DomicilioDto domicilio) {
        if (domicilio == null) {
            throw new IllegalArgumentException("Los datos del domicilio no pueden ser nulos.");
        }
        if (!StringUtils.hasText(domicilio.getCalle())) {
            throw new IllegalArgumentException("La calle del domicilio es obligatoria.");
        }
        if (domicilio.getNumero() != null && domicilio.getNumero() < 0) {
            throw new IllegalArgumentException("El numero de la calle no puede ser negativo.");
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
