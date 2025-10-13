package com.books.demo.bussiness.logic;

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
public class AutorService {

    private final AutorRepository autorRepository;

    public AutorService(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public List<AutorDto> listarAutores() {
        return autorRepository.findAll().stream()
                .filter(autor -> autor != null && StringUtils.hasText(autor.getNombre()))
                .sorted(Comparator.comparing(AutorDto::getNombre, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(autor -> autor.getApellido() == null ? "" : autor.getApellido(),
                                String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public Optional<AutorDto> obtenerAutor(Long id) {
        return autorRepository.findById(id);
    }

    public AutorDto crearAutor(AutorDto autor) {
        validarAutor(autor);
        autor.setId(null);
        return autorRepository.save(autor);
    }

    public AutorDto actualizarAutor(Long id, AutorDto autor) {
        if (id == null) {
            throw new IllegalArgumentException("El id del autor es obligatorio.");
        }
        validarAutor(autor);
        autor.setId(id);
        return autorRepository.update(id, autor)
                .orElseThrow(() -> new ApiClientException(
                        "La API no devolvio datos al actualizar el autor con id " + id + "."));
    }

    public void eliminarAutor(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("El id del autor es obligatorio.");
        }
        autorRepository.deleteById(id);
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
}
