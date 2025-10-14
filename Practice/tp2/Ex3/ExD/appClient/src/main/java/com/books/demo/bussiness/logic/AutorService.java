package com.books.demo.bussiness.logic;

import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.logic.service.BaseService;
import com.books.demo.client.dto.AutorDto;
import com.books.demo.client.exception.ApiClientException;
import com.books.demo.repository.AutorRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AutorService implements BaseService<AutorDto> {

    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    @Override
    public AutorDto alta(AutorDto autor) throws ErrorServiceException {
        try {
            validarAutor(autor);
        } catch (IllegalArgumentException e) {
            throw new ErrorServiceException(e.getMessage(), e);
        }
        autor.setId(null);
        autor.setEliminado(false);
        try {
            return autorRepository.save(autor);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo registrar el autor.", e);
        }
    }

    @Override
    public Optional<AutorDto> modificar(Long id, AutorDto autor) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id del autor es obligatorio.");
        }
        try {
            validarAutor(autor);
        } catch (IllegalArgumentException e) {
            throw new ErrorServiceException(e.getMessage(), e);
        }
        autor.setId(id);
        try {
            return autorRepository.update(id, autor);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo actualizar el autor con id " + id + ".", e);
        }
    }

    @Override
    public void baja(Long id) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id del autor es obligatorio.");
        }
        try {
            autorRepository.deleteById(id);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo eliminar el autor con id " + id + ".", e);
        }
    }

    @Override
    public Optional<AutorDto> obtener(Long id) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id del autor es obligatorio.");
        }
        try {
            return autorRepository.findById(id)
                    .filter(this::autorValido);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("Error al consultar el autor con id " + id + ".", e);
        }
    }

    @Override
    public AutorDto obtenerEntidad(Long id) throws ErrorServiceException {
        return obtener(id)
                .orElseThrow(() -> new ErrorServiceException("El autor con id " + id + " no existe."));
    }

    @Override
    public List<AutorDto> listarActivos() throws ErrorServiceException {
        try {
            return autorRepository.findAll().stream()
                    .filter(this::autorValido)
                    .sorted(Comparator.comparing(AutorDto::getNombre, String.CASE_INSENSITIVE_ORDER)
                            .thenComparing(autor -> autor.getApellido() == null ? "" : autor.getApellido(),
                                    String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo obtener el listado de autores.", e);
        }
    }

    public List<AutorDto> listarAutores() {
        try {
            return listarActivos();
        } catch (ErrorServiceException e) {
            throw asApiClientException("No se pudo obtener el listado de autores.", e);
        }
    }

    public Optional<AutorDto> obtenerAutor(Long id) {
        try {
            return obtener(id);
        } catch (ErrorServiceException e) {
            throw asApiClientException("No se pudo obtener el autor con id " + id + ".", e);
        }
    }

    public AutorDto crearAutor(AutorDto autor) {
        try {
            return alta(autor);
        } catch (ErrorServiceException e) {
            throw propagate("No se pudo registrar el autor.", e);
        }
    }

    public AutorDto actualizarAutor(Long id, AutorDto autor) {
        try {
            return modificar(id, autor)
                    .orElseThrow(() -> new ApiClientException(
                            "La API no devolvio datos al actualizar el autor con id " + id + "."));
        } catch (ErrorServiceException e) {
            throw propagate("No se pudo actualizar el autor.", e);
        }
    }

    public void eliminarAutor(Long id) {
        try {
            baja(id);
        } catch (ErrorServiceException e) {
            throw propagate("No se pudo eliminar el autor.", e);
        }
    }

    private boolean autorValido(AutorDto autor) {
        return autor != null && StringUtils.hasText(autor.getNombre());
    }

    private void validarAutor(AutorDto autor) {
        if (autor == null) {
            throw new IllegalArgumentException("Los datos del autor no pueden ser nulos.");
        }
        if (!StringUtils.hasText(autor.getNombre())) {
            throw new IllegalArgumentException("El nombre del autor es obligatorio.");
        }
        if (!StringUtils.hasText(autor.getApellido())) {
            throw new IllegalArgumentException("El apellido del autor es obligatorio.");
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
