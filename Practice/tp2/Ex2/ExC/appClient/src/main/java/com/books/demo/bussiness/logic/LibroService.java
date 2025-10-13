package com.books.demo.bussiness.logic;

import com.books.demo.client.dto.ArchivoLibroDto;
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
import org.springframework.web.multipart.MultipartFile;

@Service
public class LibroService {

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    public List<LibroDto> listarLibros() {
        return libroRepository.findAll().stream()
                .filter(libro -> libro != null && StringUtils.hasText(libro.getTitulo()))
                .map(this::normalizarLibro)
                .collect(Collectors.toList());
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
        return libroRepository.findById(id).map(this::normalizarLibro);
    }

    public LibroDto crearLibro(LibroDto libro, MultipartFile archivoPdf) {
        validarLibro(libro);
        libro.setId(null);
        libro.setAutoresIds(normalizarAutores(libro.getAutoresIds()));
        LibroDto creado = normalizarLibro(libroRepository.save(libro));
        if (creado != null && archivoPdf != null && !archivoPdf.isEmpty()) {
            creado.setArchivo(guardarArchivoInterno(creado.getId(), archivoPdf));
        }
        return creado;
    }

    public LibroDto actualizarLibro(Long id, LibroDto libro, MultipartFile archivoPdf) {
        if (id == null) {
            throw new IllegalArgumentException("El id del libro es obligatorio.");
        }
        validarLibro(libro);
        libro.setId(id);
        libro.setAutoresIds(normalizarAutores(libro.getAutoresIds()));
        LibroDto actualizado = libroRepository.update(id, libro)
                .map(this::normalizarLibro)
                .orElseThrow(() -> new ApiClientException(
                        "La API no devolvio datos al actualizar el libro con id " + id + "."));
        if (archivoPdf != null && !archivoPdf.isEmpty()) {
            actualizado.setArchivo(guardarArchivoInterno(id, archivoPdf));
        } else {
            actualizado.setArchivo(libroRepository.obtenerArchivo(id).orElse(actualizado.getArchivo()));
        }
        return actualizado;
    }

    public void eliminarLibro(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del libro es obligatorio.");
        }
        libroRepository.deleteById(id);
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

    public Optional<ArchivoLibroDto> obtenerArchivo(Long libroId) {
        return libroRepository.obtenerArchivo(libroId);
    }

    public byte[] descargarArchivo(Long libroId) {
        return libroRepository.descargarArchivo(libroId);
    }

    public ArchivoLibroDto guardarArchivo(Long libroId, MultipartFile archivoPdf) {
        return guardarArchivoInterno(libroId, archivoPdf);
    }

    private void validarLibro(LibroDto libro) {
        if (libro == null) {
            throw new IllegalArgumentException("Los datos del libro no pueden ser nulos.");
        }
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

    private ArchivoLibroDto guardarArchivoInterno(Long libroId, MultipartFile archivoPdf) {
        if (libroId == null) {
            throw new IllegalArgumentException("El id del libro es obligatorio para guardar el archivo.");
        }
        ArchivoLibroDto archivo = libroRepository.guardarArchivo(libroId, archivoPdf);
        if (archivo == null) {
            return libroRepository.obtenerArchivo(libroId).orElse(null);
        }
        return archivo;
    }
}
