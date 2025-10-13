package com.books.demo.bussiness.logic.service;

import com.books.demo.bussiness.domain.Autor;
import com.books.demo.bussiness.domain.Libro;
import com.books.demo.bussiness.domain.Persona;
import com.books.demo.bussiness.persistance.LibroRepository;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibroService {

    @Autowired
    private final LibroRepository libroRepository;
    @Autowired
    private final PersonaService personaService;
    @Autowired
    private final AutorService autorService;

    public LibroService(LibroRepository libroRepository,
                        PersonaService personaService,
                        AutorService autorService) {
        this.libroRepository = libroRepository;
        this.personaService = personaService;
        this.autorService = autorService;
    }

    @Transactional
    public Libro crearLibro(Libro libro) {
        try {
            validarLibro(libro);
            libro.setEliminado(false);
            return libroRepository.save(libro);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear libro", e);
        }
    }

    @Transactional
    public Libro modificarLibro(Long id, Libro datosActualizados) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id del libro no puede ser nulo");
            }
            validarLibro(datosActualizados);
            Libro libro = libroRepository.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado con id " + id));
            libro.setTitulo(datosActualizados.getTitulo());
            libro.setFecha(datosActualizados.getFecha());
            libro.setGenero(datosActualizados.getGenero());
            libro.setPaginas(datosActualizados.getPaginas());
            libro.setPersona(datosActualizados.getPersona());
            libro.setAutores(datosActualizados.getAutores());
            libro.setEliminado(datosActualizados.isEliminado());
            return libroRepository.save(libro);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al modificar libro", e);
        }
    }

    @Transactional
    public void eliminarLibro(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id del libro no puede ser nulo");
            }
            Libro libro = libroRepository.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado con id " + id));
            libro.setEliminado(true);
            libroRepository.save(libro);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar libro", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Libro> listarActivos() {
        try {
            return libroRepository.listarLibrosActivos();
        } catch (Exception e) {
            throw new RuntimeException("Error al listar libros", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Libro> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del libro no puede ser nulo");
        }
        try {
            return libroRepository.buscarPorId(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar libro", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Libro> buscarLibrosSinAsignar() {
        try {
            return libroRepository.buscarLibrosSinAsignar();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar libros sin asignar", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Libro> buscarLibrosAsignados() {
        try {
            return libroRepository.buscarLibrosAsignados();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar libros asignados", e);
        }
    }

    @Transactional(readOnly = true)
    public Persona obtenerPersona(Long personaId) {
        if (personaId == null) {
            return null;
        }
        try {
            return personaService.buscarPorId(personaId)
                    .orElseThrow(() -> new IllegalArgumentException("Persona no encontrada con id " + personaId));
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener persona", e);
        }
    }

    @Transactional(readOnly = true)
    public Set<Autor> obtenerAutores(List<Long> autoresIds) {
        if (autoresIds == null || autoresIds.isEmpty()) {
            throw new IllegalArgumentException("Debe indicar al menos un autor para el libro");
        }
        try {
            List<Autor> autores = autorService.buscarPorIds(autoresIds);
            Set<Long> encontrados = autores.stream()
                    .map(Autor::getId)
                    .collect(Collectors.toSet());
            Set<Long> solicitados = new HashSet<>(autoresIds);
            solicitados.removeAll(encontrados);
            if (!solicitados.isEmpty()) {
                throw new IllegalArgumentException("No se encontraron autores con ids: " + solicitados);
            }
            return new HashSet<>(autores);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener autores", e);
        }
    }

    private void validarLibro(Libro libro) {
        if (libro == null) {
            throw new IllegalArgumentException("El libro no puede ser nulo");
        }
        if (textoInvalido(libro.getTitulo())) {
            throw new IllegalArgumentException("El titulo es obligatorio");
        }
        if (textoInvalido(libro.getGenero())) {
            throw new IllegalArgumentException("El genero es obligatorio");
        }
        if (libro.getFecha() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        if (libro.getPaginas() == null || libro.getPaginas() <= 0) {
            throw new IllegalArgumentException("Las paginas deben ser mayores a cero");
        }
        Collection<?> autores = libro.getAutores();
        if (autores == null || autores.isEmpty()) {
            throw new IllegalArgumentException("El libro debe tener al menos un autor");
        }
    }

    private boolean textoInvalido(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
