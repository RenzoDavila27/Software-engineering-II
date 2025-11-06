package com.books.demo.controller.rest.dto;

import com.books.demo.bussiness.domain.Autor;
import com.books.demo.bussiness.domain.Libro;
import com.books.demo.bussiness.domain.Persona;
import com.books.demo.bussiness.domain.TipoLibro;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LibroDto {

    private Long id;
    private String titulo;
    private LocalDate fecha;
    private String genero;
    private Integer paginas;
    private Long personaId;
    private List<Long> autoresIds;
    private boolean eliminado;
    private TipoLibro tipo;

    public LibroDto() {
    }

    public LibroDto(Long id, String titulo, LocalDate fecha, String genero, Integer paginas,
                    Long personaId, List<Long> autoresIds, boolean eliminado, TipoLibro tipo) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.genero = genero;
        this.paginas = paginas;
        this.personaId = personaId;
        this.autoresIds = autoresIds;
        this.eliminado = eliminado;
        this.tipo = tipo;
    }

    public static LibroDto fromEntity(Libro libro) {
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

    public Libro toEntity() {
        return Libro.builder()
                .id(id)
                .titulo(titulo)
                .fecha(fecha)
                .genero(genero)
                .paginas(paginas)
                .eliminado(eliminado)
                .tipo(getTipo())
                .build();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Integer getPaginas() {
        return paginas;
    }

    public void setPaginas(Integer paginas) {
        this.paginas = paginas;
    }

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public List<Long> getAutoresIds() {
        return autoresIds;
    }

    public void setAutoresIds(List<Long> autoresIds) {
        this.autoresIds = autoresIds;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    public TipoLibro getTipo() {
        return tipo != null ? tipo : TipoLibro.FISICO;
    }

    public void setTipo(TipoLibro tipo) {
        this.tipo = tipo == null ? TipoLibro.FISICO : tipo;
    }
}
