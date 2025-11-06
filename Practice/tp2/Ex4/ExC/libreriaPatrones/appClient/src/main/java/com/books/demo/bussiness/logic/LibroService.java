package com.books.demo.bussiness.logic;

import com.books.demo.bussiness.logic.error.ErrorServiceException;
import com.books.demo.bussiness.logic.service.BaseService;
import com.books.demo.client.dto.LibroDto;
import com.books.demo.client.exception.ApiClientException;
import com.books.demo.repository.LibroRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class LibroService implements BaseService<LibroDto> {

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Override
    public LibroDto alta(LibroDto libro) throws ErrorServiceException {
        try {
            validarLibro(libro);
        } catch (IllegalArgumentException e) {
            throw new ErrorServiceException(e.getMessage(), e);
        }
        libro.setId(null);
        libro.setEliminado(false);
        libro.setAutoresIds(normalizarAutores(libro.getAutoresIds()));
        try {
            return normalizarLibro(libroRepository.save(libro));
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo registrar el libro.", e);
        }
    }

    @Override
    public Optional<LibroDto> modificar(Long id, LibroDto libro) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id del libro es obligatorio.");
        }
        try {
            validarLibro(libro);
        } catch (IllegalArgumentException e) {
            throw new ErrorServiceException(e.getMessage(), e);
        }
        libro.setId(id);
        libro.setAutoresIds(normalizarAutores(libro.getAutoresIds()));
        try {
            return libroRepository.update(id, libro)
                    .map(this::normalizarLibro);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo actualizar el libro con id " + id + ".", e);
        }
    }

    @Override
    public void baja(Long id) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id del libro es obligatorio.");
        }
        try {
            libroRepository.deleteById(id);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo eliminar el libro con id " + id + ".", e);
        }
    }

    @Override
    public Optional<LibroDto> obtener(Long id) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id del libro es obligatorio.");
        }
        try {
            return libroRepository.findById(id)
                    .map(this::normalizarLibro)
                    .filter(this::libroValido);
        } catch (ApiClientException e) {
            throw new ErrorServiceException("Error al consultar el libro con id " + id + ".", e);
        }
    }

    @Override
    public LibroDto obtenerEntidad(Long id) throws ErrorServiceException {
        return obtener(id)
                .orElseThrow(() -> new ErrorServiceException("El libro con id " + id + " no existe."));
    }

    @Override
    public List<LibroDto> listarActivos() throws ErrorServiceException {
        try {
            return libroRepository.findAll().stream()
                    .filter(this::libroValido)
                    .map(this::normalizarLibro)
                    .collect(Collectors.toList());
        } catch (ApiClientException e) {
            throw new ErrorServiceException("No se pudo obtener el listado de libros.", e);
        }
    }

    public List<LibroDto> listarLibros() {
        try {
            return listarActivos();
        } catch (ErrorServiceException e) {
            throw asApiClientException("No se pudo obtener el listado de libros.", e);
        }
    }

    public List<LibroDto> listarLibros(String tipo, String valor) {
        boolean filtroValido = StringUtils.hasText(tipo) && StringUtils.hasText(valor);
        if (!filtroValido) {
            return listarLibros();
        }
        try {
            List<LibroDto> resultado = libroRepository.findByFiltro(tipo.trim(), valor.trim());
            return resultado.stream()
                    .map(this::normalizarLibro)
                    .filter(this::libroValido)
                    .collect(Collectors.toList());
        } catch (ApiClientException ex) {
            throw ex;
        }
    }

    public List<LibroDto> listarLibrosPorAutor(Long autorId) {
        if (autorId == null) {
            return List.of();
        }
        return listarLibros().stream()
                .filter(libro -> libro.getAutoresIds() != null && libro.getAutoresIds().contains(autorId))
                .collect(Collectors.toList());
    }

    public List<LibroDto> listarLibrosPorPersona(Long personaId) {
        if (personaId == null) {
            return List.of();
        }
        return listarLibros().stream()
                .filter(libro -> personaId.equals(libro.getPersonaId()))
                .collect(Collectors.toList());
    }

    public Optional<LibroDto> obtenerLibro(Long id) {
        try {
            return obtener(id);
        } catch (ErrorServiceException e) {
            throw asApiClientException("No se pudo obtener el libro con id " + id + ".", e);
        }
    }

    public LibroDto crearLibro(LibroDto libro) {
        try {
            return alta(libro);
        } catch (ErrorServiceException e) {
            throw propagate("No se pudo registrar el libro.", e);
        }
    }

    public LibroDto actualizarLibro(Long id, LibroDto libro) {
        try {
            return modificar(id, libro)
                    .orElseThrow(() -> new ApiClientException(
                            "La API no devolvio datos al actualizar el libro con id " + id + "."));
        } catch (ErrorServiceException e) {
            throw propagate("No se pudo actualizar el libro.", e);
        }
    }

    public void eliminarLibro(Long id) {
        try {
            baja(id);
        } catch (ErrorServiceException e) {
            throw propagate("No se pudo eliminar el libro.", e);
        }
    }

    public void eliminarLibrosPorAutor(Long autorId) {
        listarLibrosPorAutor(autorId).forEach(libro -> libroRepository.deleteById(libro.getId()));
    }

    public void desasignarLibrosDePersona(Long personaId) {
        listarLibrosPorPersona(personaId).forEach(libro -> {
            libro.setPersonaId(null);
            libroRepository.update(libro.getId(), libro);
        });
    }

    private boolean libroValido(LibroDto libro) {
        return libro != null && StringUtils.hasText(libro.getTitulo());
    }

    private void validarLibro(LibroDto libro) {
        if (libro == null) {
            throw new IllegalArgumentException("Los datos del libro no pueden ser nulos.");
        }
        libro.setTipo(libro.getTipo());
        if (!StringUtils.hasText(libro.getTitulo())) {
            throw new IllegalArgumentException("El titulo del libro es obligatorio.");
        }
        if (libro.getPaginas() != null && libro.getPaginas() < 0) {
            throw new IllegalArgumentException("La cantidad de paginas no puede ser negativa.");
        }
    }

    private LibroDto normalizarLibro(LibroDto libro) {
        if (libro == null) {
            return null;
        }
        libro.setAutoresIds(normalizarAutores(libro.getAutoresIds()));
        libro.setTipo(libro.getTipo());
        return libro;
    }

    private List<Long> normalizarAutores(List<Long> autoresIds) {
        if (CollectionUtils.isEmpty(autoresIds)) {
            return new ArrayList<>();
        }
        return autoresIds.stream()
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
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
