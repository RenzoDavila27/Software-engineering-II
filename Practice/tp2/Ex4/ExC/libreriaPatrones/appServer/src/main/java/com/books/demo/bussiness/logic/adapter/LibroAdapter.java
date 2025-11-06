package com.books.demo.bussiness.logic.adapter;

import com.books.demo.bussiness.domain.Autor;
import com.books.demo.bussiness.domain.Libro;
import com.books.demo.bussiness.domain.Persona;
import com.books.demo.bussiness.domain.TipoLibro;
import com.books.demo.bussiness.logic.factory.LibroFactory;
import com.books.demo.controller.rest.dto.LibroDto;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class LibroAdapter implements DtoAdapter<LibroDto, Libro> {

    private final LibroFactory libroFactory;

    public LibroAdapter(LibroFactory libroFactory) {
        this.libroFactory = libroFactory;
    }

    @Override
    public LibroDto toDto(Libro libro) {
        if (libro == null) {
            return null;
        }
        Persona persona = libro.getPersona();
        Long personaId = persona != null ? persona.getId() : null;

        Collection<Autor> autores = libro.getAutores();
        List<Long> autoresIds = new ArrayList<>();
        if (autores != null) {
            for (Autor autor : autores) {
                if (autor != null) {
                    autoresIds.add(autor.getId());
                }
            }
        }

        return new LibroDto(
                libro.getId(),
                libro.getTitulo(),
                libro.getFecha(),
                libro.getGenero(),
                libro.getPaginas(),
                personaId,
                autoresIds,
                libro.isEliminado(),
                libro.getTipo()
        );
    }

    @Override
    public Libro toEntity(LibroDto dto) {
        if (dto == null) {
            return null;
        }
        Persona persona = null;
        if (dto.getPersonaId() != null) {
            persona = new Persona();
            persona.setId(dto.getPersonaId());
        }
        Set<Autor> autores = new HashSet<>();
        if (dto.getAutoresIds() != null) {
            for (Long autorId : dto.getAutoresIds()) {
                Autor autor = new Autor();
                autor.setId(autorId);
                autores.add(autor);
            }
        }
        TipoLibro tipo = dto.getTipo();
        return libroFactory.configurarTipo(
                Libro.builder()
                        .id(dto.getId())
                        .titulo(dto.getTitulo())
                        .fecha(dto.getFecha())
                        .genero(dto.getGenero())
                        .paginas(dto.getPaginas())
                        .persona(persona)
                        .autores(autores)
                        .eliminado(dto.isEliminado()),
                tipo
        ).build();
    }
}
