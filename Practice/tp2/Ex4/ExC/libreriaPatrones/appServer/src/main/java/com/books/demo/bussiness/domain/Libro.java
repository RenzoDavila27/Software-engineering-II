package com.books.demo.bussiness.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "libros")
public class Libro extends BaseEntity {

    private String titulo;

    private LocalDate fecha;

    private String genero;

    private Integer paginas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "libro_autor",
            joinColumns = @JoinColumn(name = "libro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private Set<Autor> autores = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private TipoLibro tipo;

    public Libro() {
    }

    public Libro(String titulo, LocalDate fecha, String genero, Integer paginas) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.genero = genero;
        this.paginas = paginas;
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

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Collection<Autor> getAutores() {
        return autores;
    }

    public void setAutores(Collection<Autor> autores) {
        this.autores = autores == null ? new HashSet<>() : new HashSet<>(autores);
    }

    public void addAutor(Autor autor) {
        if (autor != null) {
            this.autores.add(autor);
        }
    }

    public TipoLibro getTipo() {
        return tipo != null ? tipo : TipoLibro.FISICO;
    }

    public void setTipo(TipoLibro tipo) {
        this.tipo = tipo == null ? TipoLibro.FISICO : tipo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builderFrom(Libro libro) {
        return new Builder().from(libro);
    }

    public static final class Builder {

        private Long id;
        private String titulo;
        private LocalDate fecha;
        private String genero;
        private Integer paginas;
        private Persona persona;
        private Collection<Autor> autores = new HashSet<>();
        private Boolean eliminado;
        private TipoLibro tipo;

        private Builder() {
        }

        private Builder from(Libro libro) {
            if (libro == null) {
                return this;
            }
            this.id = libro.getId();
            this.titulo = libro.getTitulo();
            this.fecha = libro.getFecha();
            this.genero = libro.getGenero();
            this.paginas = libro.getPaginas();
            this.persona = libro.getPersona();
            Collection<Autor> autoresExistentes = libro.getAutores();
            this.autores = autoresExistentes == null ? new HashSet<>() : new HashSet<>(autoresExistentes);
            this.eliminado = libro.isEliminado();
            this.tipo = libro.getTipo();
            return this;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder titulo(String titulo) {
            this.titulo = titulo;
            return this;
        }

        public Builder fecha(LocalDate fecha) {
            this.fecha = fecha;
            return this;
        }

        public Builder genero(String genero) {
            this.genero = genero;
            return this;
        }

        public Builder paginas(Integer paginas) {
            this.paginas = paginas;
            return this;
        }

        public Builder persona(Persona persona) {
            this.persona = persona;
            return this;
        }

        public Builder autores(Collection<Autor> autores) {
            this.autores = autores == null ? new HashSet<>() : new HashSet<>(autores);
            return this;
        }

        public Builder addAutor(Autor autor) {
            if (autor != null) {
                this.autores.add(autor);
            }
            return this;
        }

        public Builder eliminado(Boolean eliminado) {
            this.eliminado = eliminado;
            return this;
        }

        public Builder tipo(TipoLibro tipo) {
            this.tipo = tipo;
            return this;
        }

        public Libro build() {
            Libro libro = new Libro();
            libro.setId(id);
            libro.setTitulo(titulo);
            libro.setFecha(fecha);
            libro.setGenero(genero);
            libro.setPaginas(paginas);
            libro.setPersona(persona);
            libro.setAutores(autores);
            if (eliminado != null) {
                libro.setEliminado(eliminado);
            }
            libro.setTipo(tipo);
            return libro;
        }
    }
}
