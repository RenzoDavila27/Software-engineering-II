package com.books.demo.controller.rest.dto;

import com.books.demo.bussiness.domain.Localidad;

public class LocalidadDto {

    private Long id;
    private String denominacion;
    private boolean eliminado;

    public LocalidadDto() {
    }

    public LocalidadDto(Long id, String denominacion, boolean eliminado) {
        this.id = id;
        this.denominacion = denominacion;
        this.eliminado = eliminado;
    }

    public static LocalidadDto fromEntity(Localidad localidad) {
        if (localidad == null) {
            return null;
        }
        return new LocalidadDto(localidad.getId(), localidad.getDenominacion(), localidad.isEliminado());
    }

    public Localidad toEntity() {
        Localidad localidad = new Localidad();
        localidad.setDenominacion(denominacion);
        localidad.setEliminado(eliminado);
        return localidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }
}

