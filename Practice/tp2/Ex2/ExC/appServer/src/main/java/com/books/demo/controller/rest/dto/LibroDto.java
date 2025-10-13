package com.books.demo.controller.rest.dto;

import com.books.demo.bussiness.domain.Autor;
import com.books.demo.bussiness.domain.Libro;
import com.books.demo.bussiness.domain.Persona;
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
    private ArchivoLibroDto archivo;

    public LibroDto() {
    }

    public LibroDto(Long id, String titulo, LocalDate fecha, String genero, Integer paginas,
                    Long personaId, List<Long> autoresIds, boolean eliminado, ArchivoLibroDto archivo) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.genero = genero;
        this.paginas = paginas;
        this.personaId = personaId;
        this.autoresIds = autoresIds;
        this.eliminado = eliminado;
        this.archivo = archivo;
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
                ArchivoLibroDto.fromEntity(libro.getArchivoLibro())
        );
    }

    public Libro toEntity() {
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setFecha(fecha);
        libro.setGenero(genero);
        libro.setPaginas(paginas);
        libro.setEliminado(eliminado);
        return libro;
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

    public ArchivoLibroDto getArchivo() {
        return archivo;
    }

    public void setArchivo(ArchivoLibroDto archivo) {
        this.archivo = archivo;
    }
}
