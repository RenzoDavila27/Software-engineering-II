package com.books.demo.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serial;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DomicilioDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String calle;
    private Integer numero;
    private Long localidadId;
    private boolean eliminado;

    public DomicilioDto() {
    }

    public DomicilioDto(Long id, String calle, Integer numero, Long localidadId, boolean eliminado) {
        this.id = id;
        this.calle = calle;
        this.numero = numero;
        this.localidadId = localidadId;
        this.eliminado = eliminado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Long getLocalidadId() {
        return localidadId;
    }

    public void setLocalidadId(Long localidadId) {
        this.localidadId = localidadId;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }
}
