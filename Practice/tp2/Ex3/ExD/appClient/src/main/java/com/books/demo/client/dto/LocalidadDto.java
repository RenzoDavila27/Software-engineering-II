package com.books.demo.client.dto;

import com.books.demo.bussiness.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serial;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocalidadDto extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String denominacion;

    public LocalidadDto() {
    }

    public LocalidadDto(Long id, String denominacion, boolean eliminado) {
        setId(id);
        this.denominacion = denominacion;
        setEliminado(eliminado);
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }
}
