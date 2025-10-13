package com.books.demo.controller.rest.dto;

import com.books.demo.bussiness.domain.Domicilio;
import com.books.demo.bussiness.domain.Localidad;

public class DomicilioDto {

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

    public static DomicilioDto fromEntity(Domicilio domicilio) {
        if (domicilio == null) {
            return null;
        }
        Localidad localidad = domicilio.getLocalidad();
        Long localidadId = localidad != null ? localidad.getId() : null;
        return new DomicilioDto(
                domicilio.getId(),
                domicilio.getCalle(),
                domicilio.getNumero(),
                localidadId,
                domicilio.isEliminado()
        );
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

