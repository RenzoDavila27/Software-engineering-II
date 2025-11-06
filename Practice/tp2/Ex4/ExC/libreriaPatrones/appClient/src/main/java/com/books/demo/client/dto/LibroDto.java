package com.books.demo.client.dto;

import com.books.demo.bussiness.domain.BaseEntity;
import com.books.demo.bussiness.domain.TipoLibro;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LibroDto extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String titulo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fecha;

    private String genero;
    private Integer paginas;
    private Long personaId;
    private List<Long> autoresIds = new ArrayList<>();
    private TipoLibro tipo = TipoLibro.FISICO;

    public LibroDto() {
    }

    public LibroDto(Long id, String titulo, LocalDate fecha, String genero,
                    Integer paginas, Long personaId, List<Long> autoresIds, boolean eliminado, TipoLibro tipo) {
        setId(id);
        this.titulo = titulo;
        this.fecha = fecha;
        this.genero = genero;
        this.paginas = paginas;
        this.personaId = personaId;
        if (autoresIds != null) {
            this.autoresIds = new ArrayList<>(autoresIds);
        }
        setEliminado(eliminado);
        setTipo(tipo);
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
        this.autoresIds = autoresIds != null ? new ArrayList<>(autoresIds) : new ArrayList<>();
    }

    public TipoLibro getTipo() {
        return tipo != null ? tipo : TipoLibro.FISICO;
    }

    public void setTipo(TipoLibro tipo) {
        this.tipo = tipo == null ? TipoLibro.FISICO : tipo;
    }
}
