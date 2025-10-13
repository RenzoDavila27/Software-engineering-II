package com.books.demo.bussiness.logic.service;

import com.books.demo.bussiness.domain.Autor;
import com.books.demo.bussiness.persistance.AutorRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutorService {

    @Autowired
    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    @Transactional
    public Autor crearAutor(Autor autor) {
        try {
            validarAutor(autor);
            autor.setEliminado(false);
            return autorRepository.save(autor);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al crear autor", e);
        }
    }

    @Transactional
    public Autor modificarAutor(Long id, Autor datosActualizados) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id del autor no puede ser nulo");
            }
            validarAutor(datosActualizados);
            Autor autor = autorRepository.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado con id " + id));
            autor.setNombre(datosActualizados.getNombre());
            autor.setApellido(datosActualizados.getApellido());
            autor.setBiografia(datosActualizados.getBiografia());
            return autorRepository.save(autor);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al modificar autor", e);
        }
    }

    @Transactional
    public void eliminarAutor(Long id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id del autor no puede ser nulo");
            }
            Autor autor = autorRepository.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado con id " + id));
            autor.setEliminado(true);
            autorRepository.save(autor);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar autor", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Autor> listarActivos() {
        try {
            return autorRepository.listarAutoresActivos();
        } catch (Exception e) {
            throw new RuntimeException("Error al listar autores", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Autor> buscarPorIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Debe indicar los identificadores de los autores");
        }
        try {
            return autorRepository.findAllById(ids);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar autores por ids", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Autor> buscarPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del autor no puede ser nulo");
        }
        try {
            return autorRepository.buscarPorId(id);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar autor", e);
        }
    }

    private void validarAutor(Autor autor) {
        if (autor == null) {
            throw new IllegalArgumentException("El autor no puede ser nulo");
        }
        if (textoInvalido(autor.getNombre())) {
            throw new IllegalArgumentException("El nombre del autor es obligatorio");
        }
        if (textoInvalido(autor.getApellido())) {
            throw new IllegalArgumentException("El apellido del autor es obligatorio");
        }
        if (textoInvalido(autor.getBiografia())) {
            throw new IllegalArgumentException("La biografia del autor es obligatoria");
        }
    }

    private boolean textoInvalido(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
